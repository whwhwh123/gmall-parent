package com.wh.gmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wh.gmall.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {
}
