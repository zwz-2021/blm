package com.chimeil.service.impl;

import com.chimeil.infrastructure.adapter.cache.CacheAdapter;
import com.chimeil.service.ShopStatusService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ShopStatusServiceImpl implements ShopStatusService {

        private final CacheAdapter cacheAdapter;

    @Override
    public Integer getStatus() {
        return cacheAdapter.getShopStatus();
    }

    @Override
    public void setStatus(Integer status) {
        cacheAdapter.setShopStatus(status);
    }
}
