package com.wh.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wh.gmall.model.product.BaseTrademark;
import org.springframework.stereotype.Service;

public interface BaseTrademarkService extends IService<BaseTrademark> {
    IPage<BaseTrademark> getPage(Page<BaseTrademark> pageParam);
}
