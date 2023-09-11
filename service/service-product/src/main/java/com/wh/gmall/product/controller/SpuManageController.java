package com.wh.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.model.product.BaseSaleAttr;
import com.wh.gmall.model.product.SpuInfo;
import com.wh.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class SpuManageController {

    @Autowired
    private ManageService manageService;

    @GetMapping("/{page}/{limit}")
    public Result<IPage<SpuInfo>> getSpuInfoPage(@PathVariable Long page,
                                 @PathVariable Long limit,
//                                 @RequestParam("category3Id")Long category3Id,
                                 SpuInfo spuInfo){
        System.out.println(spuInfo);
        Page<SpuInfo> spuInfoPage = new Page<>(page, limit);
        IPage<SpuInfo> spuInfoPageList = manageService.getSpuInfoPage(spuInfoPage, spuInfo);
        return Result.ok(spuInfoPageList);
    }

    @GetMapping("/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> baseSaleAttrList(){
        List<BaseSaleAttr> list = manageService.getBaseSaleAttrList();
        return Result.ok(list);
    }

    @PostMapping("/saveSpuInfo")
    public Result<Object> saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

}
