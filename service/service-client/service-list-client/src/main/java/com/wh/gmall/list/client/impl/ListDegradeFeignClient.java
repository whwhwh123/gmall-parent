package com.wh.gmall.list.client.impl;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.list.client.ListFeignClient;
import com.wh.gmall.model.list.SearchParam;
import com.wh.gmall.model.list.SearchResponseVo;
import org.springframework.stereotype.Component;

@Component
public class ListDegradeFeignClient implements ListFeignClient {

    @Override
    public Result list(SearchParam listParam) {
        return Result.fail();
    }

    @Override
    public Result<Void> upperGoods(Long skuId) {
        return null;
    }

    @Override
    public Result<Void> lowerGoods(Long skuId) {
        return null;
    }

    @Override
    public Result<Void> incrHotScore(Long skuId) {
        return null;
    }
}
