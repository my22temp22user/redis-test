package com.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapStorage implements KeyValueStorage {

    private final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();


    @Override
    public void persist(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String get(String value) {
        return map.get(value);
    }
}
