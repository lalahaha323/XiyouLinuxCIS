package com.controller;

import com.mapper.RedisMapper;
import com.service.AllUserList;
import com.util.ServiceResult;
import com.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@RestController
public class PushStatusController {
    @Autowired
    private AllUserList allUserList;

    @Autowired
    private RedisMapper redisMapper;

    @Autowired
    JedisPool jedisPool;

    /**
     * 每分钟发起这个请求，每分钟对redis中进行更新
     * @param onlineList
     * @return
     */
    @PostMapping("/push_status")
    public ServiceResult pushStatus(@RequestBody Set<String> onlineList){

        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();
        LocalDateTime localDateTime = LocalDateTime.now();
        for(User user : allUserList.allUserList) {
            if(onlineList.contains(user.getMac())) {
                user.setOnline(true);
                String key = "test" + DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime) + ":" + user.getId();
                long time = localDateTime.getHour() * 60 + localDateTime.getMinute();
                pipeline.setbit(key, time, true);
                //在线
            } else {
                //不在线
                user.setOnline(false);
            }
        }
        pipeline.syncAndReturnAll();
        return null;
    }
}
