package com.wh.gmall.product.controller;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.model.product.BaseTrademark;
import com.wh.gmall.model.product.CategoryTrademarkVo;
import com.wh.gmall.product.service.BaseCategoryTrademarkService;
import org.bouncycastle.est.CACertsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product/baseCategoryTrademark")
public class BaseCategoryTrademarkController {

    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;

    @GetMapping("/findTrademarkList/{category3Id}")
    public Result<List<BaseTrademark>> getTrademarkListByCategory3Id(@PathVariable Long category3Id){
        List<BaseTrademark> list = baseCategoryTrademarkService.getTrademarkList(category3Id);
        return Result.ok(list);
    }


    @DeleteMapping("/remove/{category3Id}/{trademarkId}")
    public Result<Object> remove(@PathVariable Long category3Id,
                         @PathVariable Long trademarkId){
        baseCategoryTrademarkService.removeBaseCategoryTrademarkById(category3Id, trademarkId);
        return Result.ok();
    }

    @PostMapping("/save")
    public Result<Object> save(@RequestBody CategoryTrademarkVo categoryTrademarkVo){
        baseCategoryTrademarkService.saveCategoryTrademark(categoryTrademarkVo);
        return Result.ok();
    }

    @GetMapping("/findCurrentTrademarkList/{category3Id}")
    public Result<List<BaseTrademark>> findCurrentTrademarkList(@PathVariable Long category3Id){
        List<BaseTrademark> currentTrademarkList = baseCategoryTrademarkService.findCurrentTrademarkList(category3Id);
        return Result.ok(currentTrademarkList);
    }
}
