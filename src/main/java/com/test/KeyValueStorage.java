package com.test;

public interface KeyValueStorage {

    void persist(String key, String value);
    String get(String token);
}
