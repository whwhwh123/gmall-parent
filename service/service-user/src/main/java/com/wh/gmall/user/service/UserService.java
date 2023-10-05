package com.wh.gmall.user.service;

import com.wh.gmall.model.user.UserInfo;

public interface UserService {
    /**
     * 登录
     * @param userInfo
     */
    UserInfo login(UserInfo userInfo);
}
