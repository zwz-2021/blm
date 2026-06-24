package com.chimeil.infrastructure.adapter.delivery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chimeil.exception.OrderBusinessException;
import com.chimeil.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BaiduDeliveryRangeAdapter implements DeliveryRangeAdapter {

    private static final String GEOCODING_URL = "https://api.map.baidu.com/geocoding/v3";
    private static final String DIRECTION_URL = "https://api.map.baidu.com/directionlite/v1/driving";
    private static final int MAX_DELIVERY_DISTANCE_METERS = 5000;

    @Value("${chimeil.shop.address}")
    private String shopAddress;

    @Value("${chimeil.baidu.ak}")
    private String ak;

    @Override
    public void checkOutOfRange(String address) {
        Map<String, String> params = new HashMap<>();
        params.put("address", shopAddress);
        params.put("output", "json");
        params.put("ak", ak);

        String shopCoordinate = HttpClientUtil.doGet(GEOCODING_URL, params);
        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if (!"0".equals(jsonObject.getString("status"))) {
            throw new OrderBusinessException("店铺地址解析失败");
        }

        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String shopLngLat = location.getString("lat") + "," + location.getString("lng");

        params.put("address", address);
        String userCoordinate = HttpClientUtil.doGet(GEOCODING_URL, params);
        jsonObject = JSON.parseObject(userCoordinate);
        if (!"0".equals(jsonObject.getString("status"))) {
            throw new OrderBusinessException("收货地址解析失败");
        }

        location = jsonObject.getJSONObject("result").getJSONObject("location");
        String userLngLat = location.getString("lat") + "," + location.getString("lng");

        params.put("origin", shopLngLat);
        params.put("destination", userLngLat);
        params.put("steps_info", "0");

        String json = HttpClientUtil.doGet(DIRECTION_URL, params);
        jsonObject = JSON.parseObject(json);
        if (!"0".equals(jsonObject.getString("status"))) {
            throw new OrderBusinessException("配送路线规划失败");
        }

        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray routes = result.getJSONArray("routes");
        Integer distance = routes.getJSONObject(0).getInteger("distance");
        if (distance > MAX_DELIVERY_DISTANCE_METERS) {
            throw new OrderBusinessException("超出配送范围");
        }
    }
}
