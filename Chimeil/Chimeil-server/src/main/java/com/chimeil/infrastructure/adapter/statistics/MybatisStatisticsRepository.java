package com.chimeil.infrastructure.adapter.statistics;

import com.chimeil.dto.GoodsSalesDTO;
import com.chimeil.mapper.DishMapper;
import com.chimeil.mapper.OrderMapper;
import com.chimeil.mapper.SetmealMapper;
import com.chimeil.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MybatisStatisticsRepository implements StatisticsRepository {

        private final OrderMapper orderMapper;
        private final UserMapper userMapper;
        private final DishMapper dishMapper;
        private final SetmealMapper setmealMapper;

    @Override
    public Double sumOrderAmountByMap(Map map) {
        return orderMapper.sumByMap(map);
    }

    @Override
    public Integer countOrdersByMap(Map map) {
        return orderMapper.countByMap(map);
    }

    @Override
    public Integer countUsersByMap(Map map) {
        return userMapper.countByMap(map);
    }

    @Override
    public Integer countDishesByMap(Map map) {
        return dishMapper.countByMap(map);
    }

    @Override
    public Integer countSetmealsByMap(Map map) {
        return setmealMapper.countByMap(map);
    }

    @Override
    public List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end) {
        return orderMapper.getSalesTop10(begin, end);
    }
}
