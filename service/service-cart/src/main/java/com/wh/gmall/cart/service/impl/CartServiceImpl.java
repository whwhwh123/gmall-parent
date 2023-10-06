package com.wh.gmall.cart.service.impl;

import com.wh.gmall.cart.service.CartService;
import com.wh.gmall.common.constant.RedisConst;
import com.wh.gmall.common.util.DateUtil;
import com.wh.gmall.model.cart.CartInfo;
import com.wh.gmall.model.product.SkuInfo;
import com.wh.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;


    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {
        String cartKey = getCartKey(userId);
        BoundHashOperations boundHashOps = redisTemplate.boundHashOps(cartKey);

        CartInfo cartInfo = null;
        if (boundHashOps.hasKey(skuId.toString())){
            cartInfo = (CartInfo) boundHashOps.get(skuId.toString());
            cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
            cartInfo.setIsChecked(1);
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(skuId));
            cartInfo.setUpdateTime(new Date());
        } else {
            cartInfo = new CartInfo();
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
            cartInfo.setSkuPrice(skuInfo.getPrice());
        }
        boundHashOps.put(skuId.toString(), cartInfo);
    }

    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {
        List<CartInfo> noLoginCartInfoList = null;

        //  未登录
        if (!StringUtils.isEmpty(userTempId)){
            String cartKey = this.getCartKey(userTempId);
            noLoginCartInfoList  = this.redisTemplate.opsForHash().values(cartKey);
        }
        //  未登录情况下，排序返回
        if (StringUtils.isEmpty(userId)){
            if (!CollectionUtils.isEmpty(noLoginCartInfoList)){
                noLoginCartInfoList.sort((o1,o2)->{
                    //  按照更新时间：
                    return DateUtil.truncatedCompareTo(o2.getUpdateTime(),o1.getUpdateTime(), Calendar.SECOND);
                });
            }
            return noLoginCartInfoList;
        }

        //  登录情况
        List<CartInfo> LoginCartInfoList = null;
        String cartKey = this.getCartKey(userId);
        //  hset key field value;  hget key field;  hvals key ; hmset key field value field value;  hmset key map;
        BoundHashOperations<String, String, CartInfo>  boundHashOperations = this.redisTemplate.boundHashOps(cartKey);
        //  判断购物车中的field
        //  boundHashOperations.hasKey(skuId.toString);
        if (!CollectionUtils.isEmpty(noLoginCartInfoList)){
            //  循环遍历未登录购物车集合
            noLoginCartInfoList.stream().forEach(cartInfo -> {
                //  在未登录购物车中的skuId 与登录的购物车skuId 相对  skuId = 17 18
                if (boundHashOperations.hasKey(cartInfo.getSkuId().toString())){
                    //  合并业务逻辑 : skuNum + skuNum 更新时间
                    CartInfo loginCartInfo = boundHashOperations.get(cartInfo.getSkuId().toString());
                    loginCartInfo.setSkuNum(loginCartInfo.getSkuNum()+cartInfo.getSkuNum());
                    loginCartInfo.setUpdateTime(new Date());
                    //  最新价格
                    loginCartInfo.setSkuPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));

                    //  选中状态合并
                    if (cartInfo.getIsChecked() == 1){
                        loginCartInfo.setIsChecked(1);
                    }
                    //  修改缓存的数据：    hset key field value
                    boundHashOperations.put(cartInfo.getSkuId().toString(), loginCartInfo);
                } else {
                    //  直接添加到缓存    skuId = 19
                    cartInfo.setUserId(userId);
                    cartInfo.setCreateTime(new Date());
                    cartInfo.setUpdateTime(new Date());
                    boundHashOperations.put(cartInfo.getSkuId().toString(),cartInfo);
                }
            });
            //  删除未登录购物车数据
            this.redisTemplate.delete(this.getCartKey(userTempId));
        }

        //  获取到合并之后的数据：
        LoginCartInfoList = this.redisTemplate.boundHashOps(cartKey).values();
        if (CollectionUtils.isEmpty(LoginCartInfoList)){
            return new ArrayList<>();
        }

        //  设置合并之后的排序结果！
        LoginCartInfoList.sort(((o1, o2) -> {
            return DateUtil.truncatedCompareTo(o2.getUpdateTime(),o1.getUpdateTime(), Calendar.SECOND);
        }));

        return LoginCartInfoList;
    }

    @Override
    public void checkCart(String userId, Integer isChecked, Long skuId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOps = this.redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo = boundHashOps.get(skuId.toString());
        if(null != cartInfo) {
            cartInfo.setIsChecked(isChecked);
            boundHashOps.put(skuId.toString(), cartInfo);
        }
    }

    @Override
    public void deleteCart(Long skuId, String userId) {
        BoundHashOperations<String, String, CartInfo> boundHashOps = this.redisTemplate.boundHashOps(this.getCartKey(userId));
        //  判断购物车中是否有该商品！
        if (boundHashOps.hasKey(skuId.toString())){
            boundHashOps.delete(skuId.toString());
        }
    }

    private String getCartKey(String id){
        return RedisConst.USER_KEY_PREFIX + id + RedisConst.USER_CART_KEY_SUFFIX;
    }
}
