package com.wh.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wh.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    List<SpuSaleAttr> selectSpuSaleAttrList(Long spuId);
}
