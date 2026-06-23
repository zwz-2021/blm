package com.chimeil.service;

import com.chimeil.dto.UserLoginDTO;
import com.chimeil.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
