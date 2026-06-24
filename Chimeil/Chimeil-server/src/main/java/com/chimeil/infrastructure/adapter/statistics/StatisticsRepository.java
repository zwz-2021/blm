package com.chimeil.infrastructure.adapter.statistics;

import com.chimeil.dto.GoodsSalesDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticsRepository {

    Double sumOrderAmountByMap(Map map);

    Integer countOrdersByMap(Map map);

    Integer countUsersByMap(Map map);

    Integer countDishesByMap(Map map);

    Integer countSetmealsByMap(Map map);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
