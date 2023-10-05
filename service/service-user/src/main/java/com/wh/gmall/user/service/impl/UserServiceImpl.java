package com.wh.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wh.gmall.model.user.UserInfo;
import com.wh.gmall.user.mapper.UserMapper;
import com.wh.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserInfo login(UserInfo userInfo) {

        String password = userInfo.getPasswd();
        String encrypted = DigestUtils.md5DigestAsHex(password.getBytes());

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", userInfo.getLoginName());
        queryWrapper.eq("passwd", encrypted);

        return userMapper.selectOne(queryWrapper);
    }
}
