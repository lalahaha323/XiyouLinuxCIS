package com.controller;

import com.service.AllUserList;
import com.service.PushStatusService;
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
import java.util.Set;

@RestController
public class PushStatusController {

    @Autowired
    PushStatusService pushStatusService;

    @PostMapping("/push_status")
    public ServiceResult pushStatus(@RequestBody Set<String> onlineList){
        return pushStatusService.pushStatus(onlineList);
    }
}
