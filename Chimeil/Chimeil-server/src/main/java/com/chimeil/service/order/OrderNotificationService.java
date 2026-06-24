package com.chimeil.service.order;

import com.chimeil.entity.Orders;
import com.chimeil.infrastructure.adapter.notification.NotificationAdapter;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderNotificationService {

        private final NotificationAdapter notificationAdapter;

    public void notifyPaid(Orders orders) {
        notificationAdapter.notifyNewOrder(orders.getId(), orders.getNumber());
    }

    public void notifyReminder(Orders orders) {
        notificationAdapter.notifyReminder(orders.getId(), orders.getNumber());
    }

    public void notifyStatusChanged(Orders orders) {
        notificationAdapter.notifyStatusChanged(orders.getId(), orders.getNumber(), orders.getStatus());
    }
}
