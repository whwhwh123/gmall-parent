package com.wh.gmall.product.client;

import com.alibaba.fastjson.JSONObject;
import com.wh.gmall.model.product.*;
import com.wh.gmall.product.client.impl.ProductDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "service-product", fallback = ProductDegradeFeignClient.class)
public interface ProductFeignClient {

    /**
     * 根据skuId获取sku信息
     * @param skuId
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 通过三级分类id查询分类信息
     * @param category3Id
     */
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id")Long category3Id);

    /**
     * 获取sku最新价格
     * @param skuId
     */
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 根据spuId，skuId 查询销售属性集合
     * @param skuId
     * @param spuId
     */
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId);

    /**
     * 根据spuId 查询map 集合属性
     * @param spuId
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId);

    /**
     * 根据spuId 获取海报数据
     * @param spuId
     */
    @GetMapping("/api/product/inner/findSpuPosterBySpuId/{spuId}")
    List<SpuPoster> getSpuPosterBySpuId(@PathVariable Long spuId);

    /**
     * 通过skuId 集合来查询数据
     * @param skuId
     */
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId);

    /**
     * 获取首页商品分类
     */
    @GetMapping("/api/product/inner/getBaseCategoryList")
    List<JSONObject> getBaseCategoryList();

    /**
     * 通过品牌id获取品牌信息
     */
    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId")Long tmId);
}
