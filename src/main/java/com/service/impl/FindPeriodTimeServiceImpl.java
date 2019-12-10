package com.service.impl;

import com.service.AllUserList;
import com.service.FindPeriodTimeService;
import com.util.ServiceResult;
import com.util.Time;
import com.util.User;
import com.util.UserTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 * 1.先判断endDay大于今天吗？如果大于，则返回失败
 * 2.判断startday是否小于最早数据库存的，小于的话直接从最早数据库存的那一天开始算
 */
@Slf4j
@Service
public class FindPeriodTimeServiceImpl implements FindPeriodTimeService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    AllUserList allUserList;

    @Override
    public ServiceResult findPeriod(String startDay, String endDay) {

        /**
         * jedis,pipeline的初始化
         */
        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();
        /**
         * 计算今天的日期
         */
        LocalDateTime localDateTime = LocalDateTime.now();
        String nowDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime);
        /**
         * 最后返回的结果,从数据库中查每个人每天在线的总时长
         */
        List<UserTime> allUserTimeList = new ArrayList<>();
        /**
         * 从数据库中看到总共有多少个用户
         */
        User[] users =  allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);
        for(User user : users) {
            UserTime userTime = new UserTime();
            List<Time> timeList = new ArrayList<>();
            userTime.setId(user.getId());
            userTime.setName(user.getName());
            List<Map<String, Object>> db_users = jdbcTemplate.queryForList("SELECT timeday,alltime FROM daytime WHERE id = ?", user.getId());
            for(Map userTimeMap : db_users) {
                int alltime = (int) userTimeMap.get("alltime");
                String timeday = (String) userTimeMap.get("timeday");
                /**
                 * 数据库中查询到的日期必须大于等于起始日期，并且小于终止日期才能存放
                 */
                if((timeday.compareTo(startDay) == 0) || (timeday.compareTo(startDay) > 0) || (timeday.compareTo(endDay) < 0)) {
                    Time time = new Time();
                    time.setAllTime(alltime);
                    time.setTimeDay(timeday);
                    timeList.add(time);
                }
            }
            userTime.setTimeList(timeList);
            allUserTimeList.add(userTime);
        }
        /**
         * 如果endDay<nowDay,返回-1
         * 如果endDay==nowDay，返回0
         * 如果endDay>nowDay，返回1
         */
        if(endDay.compareTo(nowDay) == 0) {
            /**
             * 说明数据要查询redis中的
             */
            //从redis中获取每个人的信息，然后传入到数据库中
            String[] keys = new String[users.length];
            int i = 0;
            for(User user : users) {
                keys[i] = (nowDay + ":" + user.getId());
                pipeline.bitcount(keys[i]);
                i++;
            }
            List<Object> alltime = pipeline.syncAndReturnAll();
            for(i = 0; i < users.length; i++) {

                Time time = new Time();
                time.setAllTime((int)((long) alltime.get(i)));
                time.setTimeDay(nowDay);
                List<Time> all = allUserTimeList.get(i).getTimeList();
                all.add(time);
            }
        }
        jedis.close();
        return ServiceResult.success(allUserTimeList);
    }
}
