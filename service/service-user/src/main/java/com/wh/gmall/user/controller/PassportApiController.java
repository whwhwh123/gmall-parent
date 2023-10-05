package com.wh.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.wh.gmall.common.constant.RedisConst;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.common.util.IpUtil;
import com.wh.gmall.model.user.UserInfo;
import com.wh.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result Login(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response){
        UserInfo user = userService.login(userInfo);

        if (user != null){
            String token = UUID.randomUUID().toString().replace("-", "");
            HashMap<String, Object> map = new HashMap<>();
            map.put("nickName", user.getNickName());
            map.put("token", token);

            JSONObject userJson = new JSONObject();
            userJson.put("userId", user.getId().toString());
            userJson.put("ip", IpUtil.getIpAddress(request));

            redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + token, userJson.toJSONString(),
                                                RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
            return Result.ok(map);
        } else {
            return Result.fail().message("用户名或密码错误");
        }
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request){
        redisTemplate.delete(RedisConst.USER_LOGIN_KEY_PREFIX + request.getHeader("token"));
        return Result.ok();
    }

}
