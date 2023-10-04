package com.wh.gmall.list.service;

import com.wh.gmall.model.list.SearchParam;
import com.wh.gmall.model.list.SearchResponseAttrVo;
import com.wh.gmall.model.list.SearchResponseVo;

import java.io.IOException;

public interface SearchService {

    /**
     * 上架商品
     * @param skuId
     */
    void upperGoods(Long skuId);

    /**
     * 下架商品
     * @param skuId
     */
    void lowerGoods(Long skuId);

    /**
     * 更新热点
     * @param skuId
     */
    void incrHotScore(Long skuId);

    /**
     * 搜索商品
     * @param searchParam
     */
    SearchResponseVo search(SearchParam searchParam) throws IOException;
}
