package com.chimeil.infrastructure.adapter.payment;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class WeChatPaymentAdapter implements PaymentAdapter {

    // Demo mode: real WeChat/Alipay payment APIs are disabled until the project has a payment license.
    // @Autowired
    // private WeChatPayUtil weChatPayUtil;

    @Override
    public JSONObject createPayment(String orderNumber, BigDecimal amount, String description, String openid) throws Exception {
        JSONObject result = new JSONObject();
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
        result.put("package", "mock_prepay_id=" + orderNumber);
        result.put("signType", "MOCK");
        result.put("paySign", "mock_pay_success");
        return result;
    }

    @Override
    public String refund(String orderNumber, String refundNumber, BigDecimal refundAmount, BigDecimal totalAmount) throws Exception {
        JSONObject result = new JSONObject();
        result.put("code", "SUCCESS");
        result.put("message", "mock refund success");
        result.put("out_trade_no", orderNumber);
        result.put("out_refund_no", refundNumber);
        return result.toJSONString();
    }
}
