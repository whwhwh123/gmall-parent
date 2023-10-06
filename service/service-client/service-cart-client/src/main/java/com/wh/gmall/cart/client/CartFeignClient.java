package com.wh.gmall.cart.client;

import com.wh.gmall.cart.client.impl.CartDegradeFeignClient;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.common.util.AuthContextHolder;
import com.wh.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@FeignClient(value = "service-cart", fallback = CartDegradeFeignClient.class)
public interface CartFeignClient {
    @RequestMapping("/addToCart/{skuId}/{skuNum}")
    Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum,
                            HttpServletRequest request);

    @GetMapping("/cartList")
    Result cartList(HttpServletRequest request);

    @GetMapping("/checkCart/{skuId}/{isChecked}")
    Result checkCart(@PathVariable Long skuId, @PathVariable Integer isChecked, HttpServletRequest request);

    @DeleteMapping("deleteCart/{skuId}")
    Result deleteCart(@PathVariable("skuId") Long skuId, HttpServletRequest request);
}
