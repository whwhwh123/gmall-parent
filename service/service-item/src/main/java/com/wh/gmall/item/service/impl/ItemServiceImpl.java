package com.wh.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.wh.gmall.item.service.ItemService;
import com.wh.gmall.model.product.*;
import com.wh.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String, Object> result = new HashMap<>();

        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        if (null != skuInfo){
            result.put("skuInfo", skuInfo);

            BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", baseCategoryView);

            List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrList);

            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuId);
            String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", valuesSkuJson);
        }

        BigDecimal price = productFeignClient.getSkuPrice(skuId);
        result.put("price", price);

        List<SpuPoster> posterList = productFeignClient.getSpuPosterBySpuId(skuId);
        result.put("spuPosterList", posterList);

        List<BaseAttrInfo> attrInfoList = productFeignClient.getAttrList(skuId);
        List<Map<String, String>> skuAttrList = attrInfoList.stream().map(baseAttrInfo -> {
            Map<String, String> attrMap = new HashMap<>();
            attrMap.put("attrName", baseAttrInfo.getAttrName());
            attrMap.put("attrValue", baseAttrInfo.getAttrValueList().get(0).getValueName());
            return attrMap;
        }).collect(Collectors.toList());
        result.put("skuAttrList", skuAttrList);

        return result;
    }
}
