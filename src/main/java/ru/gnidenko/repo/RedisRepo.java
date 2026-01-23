package ru.gnidenko.repo;

import redis.clients.jedis.Jedis;
import ru.gnidenko.config.RedisConfig;

public class RedisRepo {
    private final Jedis jedis;
    private final int expire;

    public RedisRepo(int expire) {
        jedis = RedisConfig.getJedis();
        this.expire = expire;
    }

    public void addCache(String key, String value) {
        jedis.setex(key, expire, value);
    }

    public String getCache(String key) {
        return jedis.get(key);
    }

    public void removeCache(String key) {
        jedis.del(key);
    }

    public void close(){
        jedis.close();
    }
}
