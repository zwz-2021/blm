package com.chimeil.service.order;

import com.chimeil.constant.MessageConstant;
import com.chimeil.entity.Orders;
import com.chimeil.exception.OrderBusinessException;
import org.springframework.stereotype.Component;

@Component
public class OrderStateMachine {

    public void assertTransition(Integer currentStatus, Integer targetStatus) {
        if (!canTransition(currentStatus, targetStatus)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    public boolean canTransition(Integer currentStatus, Integer targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }
        if (Orders.PENDING_PAYMENT.equals(currentStatus)) {
            return Orders.TO_BE_CONFIRMED.equals(targetStatus)
                    || Orders.CANCELLED.equals(targetStatus);
        }
        if (Orders.TO_BE_CONFIRMED.equals(currentStatus)) {
            return Orders.CONFIRMED.equals(targetStatus)
                    || Orders.CANCELLED.equals(targetStatus);
        }
        if (Orders.CONFIRMED.equals(currentStatus)) {
            return Orders.DELIVERY_IN_PROGRESS.equals(targetStatus)
                    || Orders.CANCELLED.equals(targetStatus);
        }
        if (Orders.DELIVERY_IN_PROGRESS.equals(currentStatus)) {
            return Orders.COMPLETED.equals(targetStatus);
        }
        return false;
    }
}
