package com.wh.gmall.item.client.impl;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.item.client.ItemFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ItemDegradeFeignClient implements ItemFeignClient {

    @Override
    public Result<Map<String,Object>> getItem(Long skuId) {
        return Result.fail();
    }
}
