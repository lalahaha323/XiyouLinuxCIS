package com.service;

import com.util.User;
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
    AllUserList allUserList;

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
        User[] users =  allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);
        String[] keys = new String[users.length];
        int i = 0;
        for(User user : users) {
            keys[i] = (dateNow + ":" + user.getId());
            pipeline.bitcount(keys[i]);
            i++;
        }
        List<Object> alltime = pipeline.syncAndReturnAll();

        for(i = 0; i < users.length; i++) {
            long time = (long) alltime.get(i);
            int result = jdbcTemplate.update("INSERT INTO daytime VALUES(?,?,?,?)",
                    users[i].getId(),
                    users[i].getName(),
                    dateNow,
                    time
            );
        }
    }
}
