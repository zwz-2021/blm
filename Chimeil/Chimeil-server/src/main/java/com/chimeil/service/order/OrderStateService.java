package com.chimeil.service.order;

import com.chimeil.constant.MessageConstant;
import com.chimeil.dto.OrdersCancelDTO;
import com.chimeil.dto.OrdersConfirmDTO;
import com.chimeil.dto.OrdersRejectionDTO;
import com.chimeil.entity.Orders;
import com.chimeil.exception.OrderBusinessException;
import com.chimeil.infrastructure.adapter.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 订单状态流转服务 —— 负责接单、拒单、取消、派送、完成、催单等履约操作。
 * 从 OrderApplicationService 中按职责拆分，降低单类复杂度。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderStateService {

    private final OrderRepository orderRepository;
    private final OrderStateMachine orderStateMachine;
    private final OrderNotificationService orderNotificationService;

    /**
     * 接单
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders ordersDB = getOrderOrThrow(ordersConfirmDTO.getId());
        orderStateMachine.assertTransition(ordersDB.getStatus(), Orders.CONFIRMED);

        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        updateOrderByCurrentStatus(orders, ordersDB);
    }

    /**
     * 拒单（需退款处理）
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders ordersDB = getOrderOrThrow(ordersRejectionDTO.getId());
        if (!Orders.TO_BE_CONFIRMED.equals(ordersDB.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orderStateMachine.assertTransition(ordersDB.getStatus(), Orders.CANCELLED);

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        if (Orders.PAID.equals(ordersDB.getPayStatus())) {
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        updateOrderByCurrentStatus(orders, ordersDB);
    }

    /**
     * 管理端取消订单（需退款处理）
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders ordersDB = getOrderOrThrow(ordersCancelDTO.getId());
        orderStateMachine.assertTransition(ordersDB.getStatus(), Orders.CANCELLED);

        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        if (Orders.PAID.equals(ordersDB.getPayStatus())) {
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        updateOrderByCurrentStatus(orders, ordersDB);
    }

    /**
     * 用户端取消订单
     */
    public void userCancelById(Long id) {
        Orders ordersDB = getOrderOrThrow(id);
        if (!Orders.PENDING_PAYMENT.equals(ordersDB.getStatus())
                && !Orders.TO_BE_CONFIRMED.equals(ordersDB.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orderStateMachine.assertTransition(ordersDB.getStatus(), Orders.CANCELLED);

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            orders.setPayStatus(Orders.REFUND);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        updateOrderByCurrentStatus(orders, ordersDB);
    }

    /**
     * 派送订单
     */
    public void delivery(Long id) {
        Orders ordersDB = getOrderOrThrow(id);
        orderStateMachine.assertTransition(ordersDB.getStatus(), Orders.DELIVERY_IN_PROGRESS);

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        updateOrderByCurrentStatus(orders, ordersDB);
    }

    /**
     * 完成订单
     */
    public void complete(Long id) {
        Orders ordersDB = getOrderOrThrow(id);
        orderStateMachine.assertTransition(ordersDB.getStatus(), Orders.COMPLETED);

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        updateOrderByCurrentStatus(orders, ordersDB);
    }

    /**
     * 客户催单
     */
    public void reminder(Long id) {
        Orders ordersDB = orderRepository.getById(id);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orderNotificationService.notifyReminder(ordersDB);
    }

    private Orders getOrderOrThrow(Long id) {
        Orders ordersDB = orderRepository.getById(id);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        return ordersDB;
    }

    private void updateOrderByCurrentStatus(Orders orders, Orders currentOrder) {
        int updated = orderRepository.updateByIdAndStatus(orders, currentOrder.getStatus());
        if (updated == 0) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (orders.getStatus() != null && !orders.getStatus().equals(currentOrder.getStatus())) {
            currentOrder.setStatus(orders.getStatus());
            orderNotificationService.notifyStatusChanged(currentOrder);
        }
    }
}
