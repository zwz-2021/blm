package com.chimeil.infrastructure.adapter.cache;

import com.chimeil.constant.StatusConstant;
import com.chimeil.entity.Setmeal;
import com.chimeil.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
public class RedisCacheAdapter implements CacheAdapter {

    private static final String DISH_LIST_KEY_PREFIX = "chimeil:dish:list:category:";
    private static final String DISH_LIST_INDEX_KEY = "chimeil:dish:list:keys";
    private static final String SETMEAL_LIST_KEY_PREFIX = "chimeil:setmeal:list:category:";
    private static final String SETMEAL_LIST_INDEX_KEY = "chimeil:setmeal:list:keys";
    private static final String SHOP_STATUS_KEY = "chimeil:shop:status";
    private static final String LEGACY_SHOP_STATUS_KEY = "SHOP_STATUS";

    private static final Duration DISH_LIST_TTL = Duration.ofMinutes(30);
    private static final Duration SETMEAL_LIST_TTL = Duration.ofMinutes(30);
    private static final Duration SHOP_STATUS_TTL = Duration.ofDays(30);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<DishVO> getDishList(Long categoryId) {
        return getList(dishListKey(categoryId));
    }

    @Override
    public void setDishList(Long categoryId, List<DishVO> dishes) {
        setIndexedValue(dishListKey(categoryId), dishes, DISH_LIST_TTL, DISH_LIST_INDEX_KEY);
    }

    @Override
    public void evictDishList(Long categoryId) {
        deleteIndexedValue(dishListKey(categoryId), DISH_LIST_INDEX_KEY);
    }

    @Override
    public void evictAllDishLists() {
        deleteIndexedValues(DISH_LIST_INDEX_KEY);
    }

    @Override
    public List<Setmeal> getSetmealList(Long categoryId) {
        return getList(setmealListKey(categoryId));
    }

    @Override
    public void setSetmealList(Long categoryId, List<Setmeal> setmeals) {
        setIndexedValue(setmealListKey(categoryId), setmeals, SETMEAL_LIST_TTL, SETMEAL_LIST_INDEX_KEY);
    }

    @Override
    public void evictSetmealList(Long categoryId) {
        deleteIndexedValue(setmealListKey(categoryId), SETMEAL_LIST_INDEX_KEY);
    }

    @Override
    public void evictAllSetmealLists() {
        deleteIndexedValues(SETMEAL_LIST_INDEX_KEY);
    }

    @Override
    public Integer getShopStatus() {
        Object value = redisTemplate.opsForValue().get(SHOP_STATUS_KEY);
        if (value == null) {
            value = redisTemplate.opsForValue().get(LEGACY_SHOP_STATUS_KEY);
            if (value != null) {
                redisTemplate.opsForValue().set(SHOP_STATUS_KEY, value, SHOP_STATUS_TTL);
                redisTemplate.delete(LEGACY_SHOP_STATUS_KEY);
            }
        }
        Integer status = toInteger(value);
        return status == null ? StatusConstant.DISABLE : status;
    }

    @Override
    public void setShopStatus(Integer status) {
        redisTemplate.opsForValue().set(SHOP_STATUS_KEY, status, SHOP_STATUS_TTL);
        redisTemplate.delete(LEGACY_SHOP_STATUS_KEY);
    }

    @Override
    public void evictShopStatus() {
        redisTemplate.delete(SHOP_STATUS_KEY);
        redisTemplate.delete(LEGACY_SHOP_STATUS_KEY);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getList(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof List) {
            return (List<T>) value;
        }
        return null;
    }

    private void setIndexedValue(String key, Object value, Duration ttl, String indexKey) {
        redisTemplate.opsForValue().set(key, value, ttl);
        redisTemplate.opsForSet().add(indexKey, key);
    }

    private void deleteIndexedValue(String key, String indexKey) {
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(indexKey, key);
    }

    private void deleteIndexedValues(String indexKey) {
        Set keys = redisTemplate.opsForSet().members(indexKey);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        redisTemplate.delete(indexKey);
    }

    private Integer toInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String dishListKey(Long categoryId) {
        return DISH_LIST_KEY_PREFIX + categoryId;
    }

    private String setmealListKey(Long categoryId) {
        return SETMEAL_LIST_KEY_PREFIX + categoryId;
    }
}
