package com.wh.gmall.list.client;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.list.client.impl.ListDegradeFeignClient;
import com.wh.gmall.model.list.Goods;
import com.wh.gmall.model.list.SearchParam;
import com.wh.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-list", fallback = ListDegradeFeignClient.class)
public interface ListFeignClient {

    /**
     * 搜索商品
     * @param listParam
     */
    @PostMapping("/api/list")
    Result list(@RequestBody SearchParam listParam);

    /**
     * 上架商品
     * @param skuId
     */
    @GetMapping("/api/list/inner/upperGoods/{skuId}")
    Result<Void> upperGoods(@PathVariable("skuId") Long skuId);

    /**
     * 下架商品
     * @param skuId
     */
    @GetMapping("/api/list/inner/lowerGoods/{skuId}")
    Result<Void> lowerGoods(@PathVariable("skuId") Long skuId);

    /**
     * 增加商品热度
     * @param skuId
     */
    @GetMapping("/api/list/inner/incrHotScore/{skuId}")
    Result<Void> incrHotScore(@PathVariable("skuId") Long skuId);
}
