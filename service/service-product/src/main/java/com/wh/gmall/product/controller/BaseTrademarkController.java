package com.wh.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.model.product.BaseTrademark;
import com.wh.gmall.product.service.BaseTrademarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/{page}/{limit}")
    public Result<IPage<BaseTrademark>> getBaseTrademarkPage(@PathVariable Long page,
                                                             @PathVariable Long limit){
        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, limit);
        IPage<BaseTrademark> baseTrademarkPageList = baseTrademarkService.getPage(baseTrademarkPage);
        return Result.ok(baseTrademarkPageList);
    }

    @ApiOperation(value = "根据id获取品牌对象")
    @GetMapping("/get/{id}")
    public Result<BaseTrademark> getBaseTrademarkById(@PathVariable Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    @ApiOperation(value = "新增品牌")
    @PostMapping("/save")
    public Result<Object> save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @ApiOperation(value = "修改品牌")
    @PutMapping("/update")
    public Result<Object> update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    @ApiOperation(value = "删除品牌")
    @DeleteMapping("remove/{id}")
    public Result<Object> remove(@PathVariable Long id) {
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

}
