package com.chimeil.infrastructure.adapter.cart;

import com.chimeil.entity.ShoppingCart;
import com.chimeil.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MybatisShoppingCartAdapter implements ShoppingCartAdapter {

        private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public List<ShoppingCart> list(ShoppingCart shoppingCart) {
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void updateNumberById(ShoppingCart shoppingCart) {
        shoppingCartMapper.updateNumberById(shoppingCart);
    }

    @Override
    public void insertOrIncrease(ShoppingCart shoppingCart) {
        shoppingCartMapper.insertOrIncrease(shoppingCart);
    }

    @Override
    @Transactional
    public void decreaseOrDelete(ShoppingCart shoppingCart) {
        int changedRows = shoppingCartMapper.decreaseNumber(shoppingCart);
        if (changedRows == 0) {
            shoppingCartMapper.deleteOneIfNumberEqualsOne(shoppingCart);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        shoppingCartMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteById(Long id) {
        shoppingCartMapper.deleteById(id);
    }

    @Override
    public void insertBatch(List<ShoppingCart> shoppingCartList) {
        shoppingCartMapper.insertBatch(shoppingCartList);
    }
}
