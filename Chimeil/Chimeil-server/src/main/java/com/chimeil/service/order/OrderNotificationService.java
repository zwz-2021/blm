package com.chimeil.service.order;

import com.chimeil.entity.Orders;
import com.chimeil.infrastructure.adapter.notification.NotificationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationService {

    @Autowired
    private NotificationAdapter notificationAdapter;

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
