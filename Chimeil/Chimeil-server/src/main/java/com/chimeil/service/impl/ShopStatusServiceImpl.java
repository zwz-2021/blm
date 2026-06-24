package com.chimeil.service.impl;

import com.chimeil.infrastructure.adapter.cache.CacheAdapter;
import com.chimeil.service.ShopStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopStatusServiceImpl implements ShopStatusService {

    @Autowired
    private CacheAdapter cacheAdapter;

    @Override
    public Integer getStatus() {
        return cacheAdapter.getShopStatus();
    }

    @Override
    public void setStatus(Integer status) {
        cacheAdapter.setShopStatus(status);
    }
}
