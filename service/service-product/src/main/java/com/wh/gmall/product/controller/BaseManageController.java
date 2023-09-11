package com.wh.gmall.product.controller;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.model.product.*;
import com.wh.gmall.product.mapper.BaseAttrInfoMapper;
import com.wh.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品基础属性接口")
@RestController
@RequestMapping("admin/product")
public class BaseManageController{

    @Autowired
    private ManageService manageService;

    @GetMapping("/getCategory1")
    public Result<List<BaseCategory1>> getCategory1(){
        List<BaseCategory1> list = manageService.getCategory1();
        return Result.ok(list);
    }

    @GetMapping("/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> list = manageService.getCategory2(category1Id);
        return Result.ok(list);
    }

    @GetMapping("/getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> list = manageService.getCategory3(category2Id);
        return Result.ok(list);
    }

    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> baseAttrInfoList(@PathVariable Long category1Id,
                                                       @PathVariable Long category2Id,
                                                       @PathVariable Long category3Id){
        List<BaseAttrInfo> list = manageService.getAttrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(list);
    }

    /**
     * 保存平台属性方法
     * @param baseAttrInfo
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @GetMapping("/getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable Long attrId){
        BaseAttrInfo baseAttrInfo = manageService.getAttrInfo(attrId);
        List<BaseAttrValue> list = baseAttrInfo.getAttrValueList();
        return Result.ok(list);
    }
}
