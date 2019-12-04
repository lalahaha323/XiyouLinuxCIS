package com.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

/**
 * @author lala
 */
@Repository
public class RedisMapper {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void setBitKey(String key, long time, boolean flag) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.setBit(key, time, flag);
    }
}
