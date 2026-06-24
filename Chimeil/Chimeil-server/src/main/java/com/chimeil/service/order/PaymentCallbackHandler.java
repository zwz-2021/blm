package com.chimeil.service.order;

import com.chimeil.constant.MessageConstant;
import com.chimeil.entity.Orders;
import com.chimeil.exception.OrderBusinessException;
import com.chimeil.infrastructure.adapter.order.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentCallbackHandler {

        private final OrderRepository orderRepository;
        private final OrderStateMachine orderStateMachine;
        private final OrderNotificationService orderNotificationService;

    @Transactional
    public boolean handlePaySuccess(String orderNumber) {
        Orders ordersDB = orderRepository.getByNumber(orderNumber);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (Orders.PAID.equals(ordersDB.getPayStatus())) {
            return false;
        }

        orderStateMachine.assertTransition(ordersDB.getStatus(), Orders.TO_BE_CONFIRMED);

        int updated = orderRepository.updatePaySuccess(
                ordersDB.getId(),
                Orders.PENDING_PAYMENT,
                Orders.UN_PAID,
                LocalDateTime.now());
        if (updated == 0) {
            Orders latest = orderRepository.getById(ordersDB.getId());
            if (latest != null && Orders.PAID.equals(latest.getPayStatus())) {
                return false;
            }
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        ordersDB.setStatus(Orders.TO_BE_CONFIRMED);
        ordersDB.setPayStatus(Orders.PAID);
        orderNotificationService.notifyPaid(ordersDB);
        return true;
    }
}
