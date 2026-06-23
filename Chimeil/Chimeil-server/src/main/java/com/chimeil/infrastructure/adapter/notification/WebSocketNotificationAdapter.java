package com.chimeil.infrastructure.adapter.notification;

import com.alibaba.fastjson.JSON;
import com.chimeil.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketNotificationAdapter implements NotificationAdapter {

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public void notifyNewOrder(Long orderId, String orderNumber) {
        sendOrderMessage(1, orderId, "订单号：" + orderNumber);
    }

    @Override
    public void notifyReminder(Long orderId, String orderNumber) {
        sendOrderMessage(2, orderId, "订单号：" + orderNumber);
    }

    @Override
    public void notifyStatusChanged(Long orderId, String orderNumber, Integer status) {
        sendOrderMessage(3, orderId, "订单号：" + orderNumber + "，状态变更为：" + status);
    }

    private void sendOrderMessage(Integer type, Long orderId, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("orderId", orderId);
        message.put("content", content);
        webSocketServer.sendToAllClient(JSON.toJSONString(message));
    }
}
