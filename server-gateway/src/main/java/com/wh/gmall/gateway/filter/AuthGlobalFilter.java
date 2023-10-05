package com.wh.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.common.result.ResultCodeEnum;
import com.wh.gmall.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter {

    @Autowired
    private RedisTemplate redisTemplate;

    // 匹配路径的工具类
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${authUrls.url}")
    private String authUrls;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (antPathMatcher.match("/**/inner/**", path)){
            ServerHttpResponse response = exchange.getResponse();
            return out(response, ResultCodeEnum.PERMISSION);
        }

        String userId = getUserId(request);
        if("-1".equals(userId)) {
            ServerHttpResponse response = exchange.getResponse();
            return out(response,ResultCodeEnum.PERMISSION);
        }

        if (antPathMatcher.match("/api/**/auth/**", path)){
            if(StringUtils.isEmpty(userId)){
                ServerHttpResponse response = exchange.getResponse();
                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }
        }

        for (String authUrl : authUrls.split(",")){
//            System.out.println(authUrl);
            if (path.contains(authUrl) && StringUtils.isEmpty(userId)){
                ServerHttpResponse response = exchange.getResponse();
                //303状态码表示由于请求对应的资源存在着另一个URI，应使用重定向获取请求的资源
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl="+request.getURI());
                // 重定向到登录
                return response.setComplete();
            }
        }

        if (!StringUtils.isEmpty(userId)){
            request.mutate().header("userId",userId).build();
            // 将现在的request 变成 exchange对象
            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    private String getUserId(ServerHttpRequest request){
        String token = "";
        List<String> tokenList = request.getHeaders().get("token");

        if (null != tokenList){
            token = tokenList.get(0);
        } else {
            MultiValueMap<String, HttpCookie> cookieMultiValueMap = request.getCookies();
            HttpCookie cookie = cookieMultiValueMap.getFirst("token");
            if (null != cookie){
                token = URLDecoder.decode(cookie.getValue());
            }
        }

        if(!StringUtils.isEmpty(token)) {
            String userStr = (String)redisTemplate.opsForValue().get("user:login:" + token);
            JSONObject userJson = JSONObject.parseObject(userStr);
            String ip = userJson.getString("ip");
            String curIp = IpUtil.getGatwayIpAddress(request);
            //校验token是否被盗用
            if(ip.equals(curIp)) {
                return userJson.getString("userId");
            } else {
                //ip不一致
                return "-1";
            }
        }
        return "";
    }


    /**
     * 设置响应结果
     * @param response
     * @param permission
     */
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum permission) {
        Result<Object> result = Result.build(null, permission);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);

        DataBuffer wrap = response.bufferFactory().wrap(bits);
        //乱码处理
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        return response.writeWith(Mono.just(wrap));
    }


}
