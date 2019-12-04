package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserMap {
    public HashMap<String, String> userMap = new HashMap<>();

    @Autowired
    public UserMap(JdbcTemplate jdbcTemplate){
        List<Map<String, Object>> db_users = jdbcTemplate.queryForList("SELECT id,mac FROM user");
        for (Map user : db_users){

            userMap.put((String)user.get("mac"), (String)user.get("id"));
        }
    }
}
