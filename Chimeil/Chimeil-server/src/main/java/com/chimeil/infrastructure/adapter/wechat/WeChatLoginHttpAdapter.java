package com.chimeil.infrastructure.adapter.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chimeil.properties.WeChatProperties;
import com.chimeil.utils.HttpClientUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class WeChatLoginHttpAdapter implements WeChatLoginAdapter {

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

        private final WeChatProperties weChatProperties;

    @Override
    public String getOpenid(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN, params);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}
