package com.service.impl;

import com.service.AllUserList;
import com.service.PushStatusService;
import com.util.ServiceResult;
import com.util.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * @author lala
 */

@Service
@Slf4j
public class PushStatusServiceImpl implements PushStatusService {
    @Autowired
    private AllUserList allUserList;

    @Autowired
    JedisPool jedisPool;


    /**
     * 每分钟发起这个请求，每分钟对redis中进行更新
     * @param onlineList
     * @return
     */
    @Override
    public ServiceResult pushStatus(Set<String> onlineList) {

        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();
        LocalDateTime localDateTime = LocalDateTime.now();
        long time = localDateTime.getHour() * 60 + localDateTime.getMinute();
        System.out.println("pushStatus " + localDateTime + " " + time);
        System.out.println(onlineList);
        allUserList.onLineNumber = 0;
        for(User user : allUserList.allUserList) {
            if(onlineList.contains(user.getMac())) {
                allUserList.onLineNumber++;
                user.setOnline(true);
                String key = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime) + ":" + user.getId();
                pipeline.setbit(key, time, true);
                //在线
            } else {
                //不在线
                user.setOnline(false);
            }
        }
        List<Object> list = pipeline.syncAndReturnAll();
        jedis.close();
        localDateTime = LocalDateTime.now();
        System.out.println("redis ok " + localDateTime + " "  + list);
        return ServiceResult.success(allUserList.onLineNumber);
    }
}
