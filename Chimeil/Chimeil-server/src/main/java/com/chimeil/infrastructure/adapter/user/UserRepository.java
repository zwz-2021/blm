package com.chimeil.infrastructure.adapter.user;

import com.chimeil.entity.User;

public interface UserRepository {

    User getByOpenid(String openid);

    User getById(Long id);

    void insert(User user);
}
