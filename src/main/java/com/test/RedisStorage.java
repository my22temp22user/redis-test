package com.test;

import redis.clients.jedis.Jedis;

public class RedisStorage implements KeyValueStorage {

    // TODO use JedisPool
    private final Jedis jedis = new Jedis(Configuration.get().getRedisHost(), Configuration.get().getRedisPort());

    @Override
    public synchronized void persist(String key, String value) {
        jedis.set(key, value);
    }

    @Override
    public synchronized String get(String key) {
        return jedis.get(key);
    }
}
