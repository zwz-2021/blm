package com.chimeil.service.impl;

import com.chimeil.constant.StatusConstant;
import com.chimeil.entity.Dish;
import com.chimeil.entity.Setmeal;
import com.chimeil.infrastructure.adapter.cache.CacheAdapter;
import com.chimeil.service.DishService;
import com.chimeil.service.MenuQueryService;
import com.chimeil.service.SetmealService;
import com.chimeil.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuQueryServiceImpl implements MenuQueryService {

    @Autowired
    private CacheAdapter cacheAdapter;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public List<DishVO> listEnabledDishesByCategory(Long categoryId) {
        List<DishVO> list = cacheAdapter.getDishList(categoryId);
        if (list != null) {
            return list;
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);

        list = dishService.listWithFlavor(dish);
        cacheAdapter.setDishList(categoryId, list);
        return list;
    }

    @Override
    public List<Setmeal> listEnabledSetmealsByCategory(Long categoryId) {
        List<Setmeal> list = cacheAdapter.getSetmealList(categoryId);
        if (list != null) {
            return list;
        }

        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);

        list = setmealService.list(setmeal);
        cacheAdapter.setSetmealList(categoryId, list);
        return list;
    }

    @Override
    public Integer getShopStatus() {
        return cacheAdapter.getShopStatus();
    }
}
