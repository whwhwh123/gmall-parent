package com.wh.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.wh.gmall.model.product.*;
import com.wh.gmall.product.service.BaseTrademarkService;
import com.wh.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductApiController {

    @Autowired
    private ManageService manageService;

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @GetMapping("inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable("tmId")Long tmId){
        return baseTrademarkService.getById(tmId);
    }

    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getAttrValueList(@PathVariable("skuId") Long skuId){
        return manageService.getSkuInfo(skuId);
    }

    @GetMapping("/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryViewByCategory3Id(@PathVariable Long category3Id){
        return manageService.getBaseCategoryViewByCategory3Id(category3Id);
    }

    @GetMapping("/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId){
        return manageService.getSkuPrice(skuId);
    }

    @GetMapping("/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                          @PathVariable Long spuId){
        return manageService.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        return manageService.getSkuValueIdsMap(spuId);
    }

    @GetMapping("/inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable Long spuId){
        return manageService.findSpuPosterBySpuId(spuId);
    }

    @GetMapping("inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId){
        return manageService.getAttrList(skuId);
    }

    @GetMapping("/inner/getBaseCategoryList")
    public List<JSONObject> getBaseCategoryList(){
        return manageService.getBaseCategoryList();
    }

}
