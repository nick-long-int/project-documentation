package ru.gnidenko.config;

import redis.clients.jedis.Jedis;

public class RedisConfig {
    public static Jedis getJedis() {
        return new Jedis("localhost", 6379);
    }
}
