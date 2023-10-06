package com.wh.gmall.cart.client.impl;

import com.wh.gmall.cart.client.CartFeignClient;
import com.wh.gmall.common.result.Result;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CartDegradeFeignClient implements CartFeignClient {
    @Override
    public Result addToCart(Long skuId, Integer skuNum, HttpServletRequest request) {
        return null;
    }

    @Override
    public Result cartList(HttpServletRequest request) {
        return null;
    }

    @Override
    public Result checkCart(Long skuId, Integer isChecked, HttpServletRequest request) {
        return null;
    }

    @Override
    public Result deleteCart(Long skuId, HttpServletRequest request) {
        return null;
    }
}
