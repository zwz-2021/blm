package com.chimeil.infrastructure.adapter.cache;

import com.chimeil.entity.Setmeal;
import com.chimeil.vo.DishVO;

import java.util.List;

public interface CacheAdapter {

    List<DishVO> getDishList(Long categoryId);

    void setDishList(Long categoryId, List<DishVO> dishes);

    void evictDishList(Long categoryId);

    void evictAllDishLists();

    List<Setmeal> getSetmealList(Long categoryId);

    void setSetmealList(Long categoryId, List<Setmeal> setmeals);

    void evictSetmealList(Long categoryId);

    void evictAllSetmealLists();

    Integer getShopStatus();

    void setShopStatus(Integer status);

    void evictShopStatus();
}
