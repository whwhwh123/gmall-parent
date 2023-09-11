package com.wh.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.model.product.SkuInfo;
import com.wh.gmall.model.product.SpuImage;
import com.wh.gmall.model.product.SpuSaleAttr;
import com.wh.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品SKU接口")
@RestController
@RequestMapping("/admin/product")
public class SkuManageController {
    @Autowired
    private ManageService manageService;

    /**
     * SKU分页列表
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list/{page}/{limit}")
    public Result index(
            @PathVariable Long page,
            @PathVariable Long limit) {
        Page<SkuInfo> pageParam = new Page<>(page, limit);
        IPage<SkuInfo> pageModel = manageService.getSkuInfoPage(pageParam);
        return Result.ok(pageModel);
    }

    /**
     * 商品上架
     * @param skuId
     */
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        manageService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 商品下架
     * @param skuId
     */
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId) {
        manageService.cancelSale(skuId);
        return Result.ok();
    }


    /**
     * 根据spuId 查询spuImageList
     * @param spuId
     */
    @GetMapping("spuImageList/{spuId}")
    public Result<List<SpuImage>> getSpuImageList(@PathVariable("spuId") Long spuId) {
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }

    /**
     * 根据spuId 查询销售属性集合
     * @param spuId
     */
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("spuId") Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }

    @PostMapping("saveSkuInfo")
    public Result<Object> saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        // 调用服务层
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

}
