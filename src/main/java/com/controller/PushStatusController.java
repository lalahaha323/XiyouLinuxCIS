package com.controller;

import com.mapper.RedisMapper;
import com.service.UserMap;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class PushStatusController {
    @Autowired
    private UserMap userMap;

    @Autowired
    private RedisMapper redisMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 每分钟发起这个请求，每分钟对redis中进行更新
     * @param onlineList
     * @return
     */
    @PostMapping("/push_status")
    public ServiceResult pushStatus(@RequestBody List<String> onlineList){

        String id;
        LocalDateTime localDateTime = LocalDateTime.now();
        for(String mac : onlineList) {
            System.out.println(mac);
            id = userMap.userMap.get(mac);
            if(id != null && id.length() != 0){
                String key = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime) + ":" + id;
                long time = localDateTime.getHour() * 60 + localDateTime.getMinute();
                redisMapper.setBitKey(key, time, true);
            }
        }
        return null;
    }
}
