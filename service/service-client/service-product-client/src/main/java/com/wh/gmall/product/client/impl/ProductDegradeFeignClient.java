package com.wh.gmall.product.client.impl;

import com.alibaba.fastjson.JSONObject;
import com.wh.gmall.model.product.*;
import com.wh.gmall.product.client.ProductFeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class ProductDegradeFeignClient implements ProductFeignClient {

    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return null;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return null;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return null;
    }

    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        return null;
    }
    @Override
    public List<SpuPoster> getSpuPosterBySpuId(Long spuId) {
        return null;
    }
    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        return null;
    }
    @Override
    public List<JSONObject> getBaseCategoryList() {
        return null;
    }
}
