package com.chimeil.infrastructure.adapter.user;

import com.chimeil.entity.User;
import com.chimeil.mapper.UserMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MybatisUserRepository implements UserRepository {

        private final UserMapper userMapper;

    @Override
    public User getByOpenid(String openid) {
        return userMapper.getByOpenid(openid);
    }

    @Override
    public User getById(Long id) {
        return userMapper.getById(id);
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }
}
