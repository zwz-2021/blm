package com.chimeil.service.impl;

import com.chimeil.constant.MessageConstant;
import com.chimeil.dto.UserLoginDTO;
import com.chimeil.entity.User;
import com.chimeil.exception.LoginFailedException;
import com.chimeil.infrastructure.adapter.user.UserRepository;
import com.chimeil.infrastructure.adapter.wechat.WeChatLoginAdapter;
import com.chimeil.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

        private final WeChatLoginAdapter weChatLoginAdapter;
        private final UserRepository userRepository;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = weChatLoginAdapter.getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        User user = userRepository.getByOpenid(openid);

        //如果是新用户，自动完成注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userRepository.insert(user);
        }

        //返回这个用户对象
        return user;
    }

}
