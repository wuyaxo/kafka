package com.neoby.dm.middleware.service;

public interface RedisService {

    boolean existsKey(String key);

    void deleteKey(String key);

    String get(String key);

    void put(String key, Object document);

}
