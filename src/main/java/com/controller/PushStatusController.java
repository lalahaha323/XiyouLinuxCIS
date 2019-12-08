package com.controller;

import com.mapper.RedisMapper;
import com.service.AllUserMap;
import com.util.ServiceResult;
import com.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class PushStatusController {
    @Autowired
    private AllUserMap userMap;

    @Autowired
    private RedisMapper redisMapper;

    /**
     * 每分钟发起这个请求，每分钟对redis中进行更新
     * @param onlineList
     * @return
     */
    @PostMapping("/push_status")
    public ServiceResult pushStatus(@RequestBody List<String> onlineList){

        LocalDateTime localDateTime = LocalDateTime.now();
        for(String mac : onlineList) {
            User user = userMap.allUserMap.get(mac);
            if(user != null){
                String key = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime) + ":" + user.getId();
                long time = localDateTime.getHour() * 60 + localDateTime.getMinute();
                redisMapper.setBitKey(key, time, true);
            }
        }
        return null;
    }
}
