package com.wh.gmall.cart.service;

import com.wh.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartService {
    /**
     * 添加购物车 用户Id，商品Id，商品数量。
      * @param skuId
     * @param userId
     * @param skuNum
     */
    void addToCart(Long skuId, String userId, Integer skuNum);

    /**
     * 通过用户Id 查询购物车列表
     * @param userId
     * @param userTempId
     */
    List<CartInfo> getCartList(String userId, String userTempId);

    /**
     * 更新商品选中状态
     * @param userId
     * @param isChecked
     * @param skuId
     */
    void checkCart(String userId, Integer isChecked, Long skuId);

    /**
     * 删除购物车商品
     * @param skuId
     * @param userId
     */
    void deleteCart(Long skuId, String userId);
}
