package com.chimeil.infrastructure.adapter.payment;

import com.alibaba.fastjson.JSONObject;
import com.chimeil.utils.WeChatPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WeChatPaymentAdapter implements PaymentAdapter {

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Override
    public JSONObject createPayment(String orderNumber, BigDecimal amount, String description, String openid) throws Exception {
        return weChatPayUtil.pay(orderNumber, amount, description, openid);
    }

    @Override
    public String refund(String orderNumber, String refundNumber, BigDecimal refundAmount, BigDecimal totalAmount) throws Exception {
        return weChatPayUtil.refund(orderNumber, refundNumber, refundAmount, totalAmount);
    }
}
