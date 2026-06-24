package com.chimeil.infrastructure.adapter.notification;

public interface NotificationAdapter {

    void notifyNewOrder(Long orderId, String orderNumber);

    void notifyReminder(Long orderId, String orderNumber);

    void notifyStatusChanged(Long orderId, String orderNumber, Integer status);
}
