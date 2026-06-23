package com.chimeil.service;

import com.chimeil.entity.Setmeal;
import com.chimeil.vo.DishVO;

import java.util.List;

public interface MenuQueryService {

    List<DishVO> listEnabledDishesByCategory(Long categoryId);

    List<Setmeal> listEnabledSetmealsByCategory(Long categoryId);

    Integer getShopStatus();
}
