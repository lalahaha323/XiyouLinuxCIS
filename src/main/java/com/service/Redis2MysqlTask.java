package com.service;

import com.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author lala
 */

@Component
@Service
public class Redis2MysqlTask {

    @Autowired
    AllUserMap allUserMap;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 每天凌晨一点将redis中昨天的数据导入到数据库中
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void process() {
        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();
        String dateNow = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //从redis中获取每个人的信息，然后传入到数据库中
        UserUtil[] userUtils =  allUserMap.allUserMap.values().toArray(new UserUtil[0]);
        String[] keys = new String[userUtils.length];
        int i = 0;
        for(UserUtil userUtil : userUtils) {
            keys[i] = (dateNow + ":" + userUtil.getId());
            pipeline.bitcount(keys[i]);
            i++;
        }
        List<Object> alltime = pipeline.syncAndReturnAll();

        for(i = 0; i < userUtils.length; i++) {
            long time = (long) alltime.get(i);
            int result = jdbcTemplate.update("INSERT INTO daytime VALUES(?,?,?,?)",
                    userUtils[i].getId(),
                    userUtils[i].getName(),
                    dateNow,
                    time
            );
        }
    }
}
