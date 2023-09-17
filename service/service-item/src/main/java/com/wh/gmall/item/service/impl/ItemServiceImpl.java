package com.wh.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.wh.gmall.common.constant.RedisConst;
import com.wh.gmall.item.service.ItemService;
import com.wh.gmall.model.product.*;
import com.wh.gmall.product.client.ProductFeignClient;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String, Object> result = new HashMap<>();

//        BLOOMFILTEER
//        RBloomFilter<Long> rBloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
//        if (!rBloomFilter.contains(skuId))
//            return result;

        // 通过skuId 查询skuInfo
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                result.put("skuInfo", skuInfo);
                return skuInfo;
            }
        }, threadPoolExecutor);

        //获取skuInfo then 获取销售属性和选中状态
        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
                result.put("spuSaleAttrList", spuSaleAttrList);
            }
        }, threadPoolExecutor);

        //获取skuInfo then 获取商品切换数据
        CompletableFuture<Void> skuValueIdsMapCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
                String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
                result.put("valuesSkuJson", valuesSkuJson);
            }
        }, threadPoolExecutor);

        //获取skuInfo then 获取三级分类信息
        CompletableFuture<Void> baseCategoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                result.put("categoryView", baseCategoryView);
            }
        }, threadPoolExecutor);

        //获取skuInfo then 获取海报信息
        CompletableFuture<Void> spuPosterListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuPoster> posterList = productFeignClient.getSpuPosterBySpuId(skuInfo.getSpuId());
                result.put("spuPosterList", posterList);
            }
        }, threadPoolExecutor);

        //获取售价信息
        CompletableFuture<Void> skuPriceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            result.put("price", skuPrice);
        }, threadPoolExecutor);

        //获取平台信息
        CompletableFuture<Void> skuAttrInfoListCompletableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                List<BaseAttrInfo> attrInfoList = productFeignClient.getAttrList(skuId);
                List<Map<String, String>> skuAttrList = attrInfoList.stream().map(baseAttrInfo -> {
                    Map<String, String> attrMap = new HashMap<>();
                    attrMap.put("attrName", baseAttrInfo.getAttrName());
                    attrMap.put("attrValue", baseAttrInfo.getAttrValueList().get(0).getValueName());
                    return attrMap;
                }).collect(Collectors.toList());
                result.put("skuAttrList", skuAttrList);
            }
        }, threadPoolExecutor);

        CompletableFuture.allOf(skuInfoCompletableFuture,
                                skuValueIdsMapCompletableFuture,
                                skuPriceCompletableFuture,
                                skuAttrInfoListCompletableFuture,
                                spuSaleAttrListCompletableFuture,
                                spuPosterListCompletableFuture,
                                baseCategoryViewCompletableFuture).join();

        return result;
    }
}
