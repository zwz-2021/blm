package com.chimeil.task;

import com.chimeil.entity.Orders;
import com.chimeil.infrastructure.adapter.order.OrderRepository;
import com.chimeil.service.order.OrderNotificationService;
import com.chimeil.service.order.OrderStateMachine;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class OrderTask {

        private final OrderRepository orderRepository;
        private final OrderStateMachine orderStateMachine;
        private final OrderNotificationService orderNotificationService;

    /**
     * 处理超时订单的方法
     */
    @Scheduled(cron = "0 * * * * ? ") //每分钟触发一次
    public void processTimeoutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // select * from orders where status = ? and order_time < (当前时间 - 15分钟)
        List<Orders> ordersList = orderRepository.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if(ordersList != null && ordersList.size() > 0){
            for (Orders orders : ordersList) {
                Integer currentStatus = orders.getStatus();
                orderStateMachine.assertTransition(currentStatus, Orders.CANCELLED);
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                if (orderRepository.updateByIdAndStatus(orders, currentStatus) > 0) {
                    orderNotificationService.notifyStatusChanged(orders);
                }
            }
        }
    }

    /**
     * 处理一直处于派送中状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") //每天凌晨1点触发一次
    public void processDeliveryOrder(){
        log.info("定时处理处于派送中的订单：{}",LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> ordersList = orderRepository.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if(ordersList != null && ordersList.size() > 0){
            for (Orders orders : ordersList) {
                Integer currentStatus = orders.getStatus();
                orderStateMachine.assertTransition(currentStatus, Orders.COMPLETED);
                orders.setStatus(Orders.COMPLETED);
                if (orderRepository.updateByIdAndStatus(orders, currentStatus) > 0) {
                    orderNotificationService.notifyStatusChanged(orders);
                }
            }
        }
    }
}
