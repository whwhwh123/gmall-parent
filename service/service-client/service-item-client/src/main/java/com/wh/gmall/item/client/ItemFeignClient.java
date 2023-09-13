package com.wh.gmall.item.client;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.item.client.impl.ItemDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "service-item", fallback = ItemDegradeFeignClient.class)
public interface ItemFeignClient {

    /**
     * 商品详情
     * @param skuId
     */
    @GetMapping("/api/item/{skuId}")
    Result<Map<String,Object>> getItem(@PathVariable("skuId") Long skuId);

}
