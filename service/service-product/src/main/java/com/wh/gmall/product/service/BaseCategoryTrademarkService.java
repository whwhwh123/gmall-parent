package com.wh.gmall.product.service;

import com.wh.gmall.model.product.BaseCategoryTrademark;
import com.wh.gmall.model.product.BaseTrademark;
import com.wh.gmall.model.product.CategoryTrademarkVo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BaseCategoryTrademarkService {

    List<BaseTrademark> getTrademarkList(Long category3Id);

    void removeBaseCategoryTrademarkById(Long category3Id, Long trademarkId);

    void saveCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo);

    List<BaseTrademark> findCurrentTrademarkList(Long category3Id);
}
