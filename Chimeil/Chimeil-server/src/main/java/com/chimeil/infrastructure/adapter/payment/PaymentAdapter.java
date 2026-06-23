package com.chimeil.infrastructure.adapter.payment;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public interface PaymentAdapter {

    JSONObject createPayment(String orderNumber, BigDecimal amount, String description, String openid) throws Exception;

    String refund(String orderNumber, String refundNumber, BigDecimal refundAmount, BigDecimal totalAmount) throws Exception;
}
