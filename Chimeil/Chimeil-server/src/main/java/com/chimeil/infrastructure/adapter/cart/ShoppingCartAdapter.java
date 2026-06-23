package com.chimeil.infrastructure.adapter.cart;

import com.chimeil.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartAdapter {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void updateNumberById(ShoppingCart shoppingCart);

    void insertOrIncrease(ShoppingCart shoppingCart);

    void decreaseOrDelete(ShoppingCart shoppingCart);

    void deleteByUserId(Long userId);

    void deleteById(Long id);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
