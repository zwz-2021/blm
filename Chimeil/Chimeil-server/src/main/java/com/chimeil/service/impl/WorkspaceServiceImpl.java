package com.chimeil.service.impl;

import com.chimeil.constant.StatusConstant;
import com.chimeil.entity.Orders;
import com.chimeil.infrastructure.adapter.statistics.StatisticsRepository;
import com.chimeil.service.WorkspaceService;
import com.chimeil.vo.BusinessDataVO;
import com.chimeil.vo.DishOverViewVO;
import com.chimeil.vo.OrderOverViewVO;
import com.chimeil.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */

        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);

        //查询总订单数
        Integer totalOrderCount = statisticsRepository.countOrdersByMap(map);

        map.put("status", Orders.COMPLETED);
        //营业额
        Double turnover = statisticsRepository.sumOrderAmountByMap(map);
        turnover = turnover == null? 0.0 : turnover;

        //有效订单数
        Integer validOrderCount = statisticsRepository.countOrdersByMap(map);

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = statisticsRepository.countUsersByMap(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);

        //待接单
        Integer waitingOrders = statisticsRepository.countOrdersByMap(map);

        //待派送
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = statisticsRepository.countOrdersByMap(map);

        //已完成
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = statisticsRepository.countOrdersByMap(map);

        //已取消
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = statisticsRepository.countOrdersByMap(map);

        //全部订单
        map.put("status", null);
        Integer allOrders = statisticsRepository.countOrdersByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = statisticsRepository.countDishesByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = statisticsRepository.countDishesByMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = statisticsRepository.countSetmealsByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = statisticsRepository.countSetmealsByMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
