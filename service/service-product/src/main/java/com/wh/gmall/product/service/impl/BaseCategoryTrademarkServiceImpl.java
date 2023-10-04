package com.wh.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh.gmall.model.product.BaseCategoryTrademark;
import com.wh.gmall.model.product.BaseTrademark;
import com.wh.gmall.model.product.CategoryTrademarkVo;
import com.wh.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.wh.gmall.product.mapper.BaseTrademarkMapper;
import com.wh.gmall.product.service.BaseCategoryTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark>
                                            implements BaseCategoryTrademarkService {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;


    @Override
    public List<BaseTrademark> getTrademarkList(Long category3Id) {
        QueryWrapper<BaseCategoryTrademark> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", category3Id);
        List<BaseCategoryTrademark> categoryTrademarkList = baseCategoryTrademarkMapper.selectList(queryWrapper);

        if (!CollectionUtils.isEmpty(categoryTrademarkList)){
            List<Long> trademarkIdList = categoryTrademarkList.stream().map(
                    BaseCategoryTrademark::getTrademarkId).collect(Collectors.toList());
            return baseTrademarkMapper.selectBatchIds(trademarkIdList);
        }
        return null;
    }

    @Override
    public void removeBaseCategoryTrademarkById(Long category3Id, Long trademarkId) {
        QueryWrapper<BaseCategoryTrademark> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", category3Id);
        queryWrapper.eq("trademark_id", trademarkId);
        baseCategoryTrademarkMapper.delete(queryWrapper);
    }

    @Override
    public void saveCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo) {
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
        if (!CollectionUtils.isEmpty(trademarkIdList)){
            List<BaseCategoryTrademark> collect = trademarkIdList.stream().map(trademarkId -> {
                BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
                baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
                baseCategoryTrademark.setTrademarkId(trademarkId);
                return baseCategoryTrademark;
            }).collect(Collectors.toList());

            this.saveBatch(collect);
        }
    }

    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Long category3Id){
        QueryWrapper<BaseCategoryTrademark> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", category3Id);
        List<Long> trademarkIdList = baseCategoryTrademarkMapper.selectList(queryWrapper).stream().
                map(BaseCategoryTrademark::getTrademarkId).collect(Collectors.toList());

        List<BaseTrademark> trademarkList = baseTrademarkMapper.selectList(null);
        if (!CollectionUtils.isEmpty(trademarkList)){
            List<BaseTrademark> availableTrademarkList = trademarkList.stream().filter(trademark -> {
                return !trademarkIdList.contains(trademark.getId());
            }).collect(Collectors.toList());
            return availableTrademarkList;
        }
        return baseTrademarkMapper.selectList(null);
    }

}

