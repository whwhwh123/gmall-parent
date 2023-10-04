package com.wh.gmall.list.controller;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.list.service.SearchService;
import com.wh.gmall.model.list.Goods;
import com.wh.gmall.model.list.SearchParam;
import com.wh.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private SearchService searchService;

    @PostMapping
    public Result list(@RequestBody SearchParam searchParam) throws IOException {
        SearchResponseVo response = searchService.search(searchParam);
        return Result.ok(response);
    }

    @GetMapping("/inner/createIndex")
    public Result<Void> createIndex() {
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 上架商品
     * @param skuId
     * @return
     */
    @GetMapping("/inner/upperGoods/{skuId}")
    public Result<Void> upperGoods(@PathVariable("skuId") Long skuId) {
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @GetMapping("/inner/lowerGoods/{skuId}")
    public Result<Void> lowerGoods(@PathVariable("skuId") Long skuId) {
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    @GetMapping("/inner/incrHotScore/{skuId}")
    public Result<Void> incrHotScore(@PathVariable("skuId") Long skuId) {
        searchService.incrHotScore(skuId);
        return Result.ok();
    }


}
