package com.neoby.dm.middleware.service.impl;

import com.neoby.dm.middleware.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;
//    public static final long NOT_EXPIRE = -1;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void put(String key, Object document) {
        redisTemplate.opsForValue().set(key, document, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }


}
