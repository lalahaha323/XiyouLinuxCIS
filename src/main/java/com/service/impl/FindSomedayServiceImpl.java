package com.service.impl;

import com.mapper.RedisMapper;
import com.service.AllUserMap;
import com.service.FindSomedayService;
import com.util.OnOffLineUtil;
import com.util.ServiceResult;
import com.util.UserOnOffUtil;
import com.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */
@Service
public class FindSomedayServiceImpl implements FindSomedayService {
    @Autowired
    RedisMapper redisMapper;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private AllUserMap allUserMap;

    @Override
    public ServiceResult findRedis(String date, long time) {
        Jedis jedis = jedisPool.getResource();
        long count = 52;
        long startFlag;
        long endFlag;
        int i = 1;
        String key = null;
        List<UserOnOffUtil> userOnOffUtils = new ArrayList<>();

        for(UserUtil userUtil : allUserMap.allUserMap.values()) {
               UserOnOffUtil userOnOffUtil = new UserOnOffUtil();
               key = date + ":" + userUtil.getId();
               List<OnOffLineUtil> onOffLineUtils = new ArrayList<>();
                /**
                 * 说明在redis数据库中有这个，此刻他来签到了
                 */
                System.out.println(userUtil.getName() + "在循环");
                i = 1;
                count = 52;
                System.out.println(i % 2);
               if(jedis.exists(key)) {
                   startFlag = count % 8 - 1;
                   System.out.println(userUtil.getName() + "今天来了");
                   while(true) {
                       OnOffLineUtil onOffLineUtil = new OnOffLineUtil();
                       if(i % 2 == 0) {
                           /**
                            * 统计下线情况
                            */
                           System.out.println("起始时间: " +  count  + "终止时间: " + time);
                           BitPosParams timePeriod = new BitPosParams(count, time/8 - 1);
                           count = jedis.bitpos(key, false, timePeriod);
                           System.out.println(count + "第" + i + "次下线: " + count);
                           if(count == -1){
                               onOffLineUtil.setOffLine(time);
                               onOffLineUtils.add(onOffLineUtil);
                               break;
                           }
                           onOffLineUtil.setOffLine(count);
                           onOffLineUtils.add(onOffLineUtil);
                       } else {
                           /**
                            * 统计上线情况
                            */




                           int j = 1;
                           System.out.println("起始时间: " +  count  + "终止时间: " + time);
                           BitPosParams timePeriod = new BitPosParams(count, time/8 - 1);
                           count = jedis.bitpos(key, true, timePeriod);
                           System.out.println(count + "第" + i + "次上线: " + count);
                           if(count == -1) {
                               break;
                           }
                           onOffLineUtil.setOnLine(count);
                           for(startFlag = time % 8 + 1 ;startFlag > 0; startFlag--) {
                               if(jedis.getbit(key, time - startFlag + 1)){
                                   if(j == 1) {
                                       j = 0;
                                   } else {
                                       onOffLineUtil.setOnLine(count);
                                   }
                                   i++;
                               } else {
                                   onOffLineUtil.setOffLine(count);
                                   onOffLineUtils.add(onOffLineUtil);
                               }
                           }
                       }
                       i++;
                   }
               } else {
                   /**
                    * 说明在redis数据库中没有这个，此刻他没有来签到
                    */
                   System.out.println(userUtil.getName() + "今天没来");
                   System.out.println(userUtil.getName());
               }
               userOnOffUtil.setOnOffLineUtils(onOffLineUtils);
               userOnOffUtil.setUserUtil(userUtil);

               userOnOffUtils.add(userOnOffUtil);
        }
        return ServiceResult.success(userOnOffUtils);
    }

    @Override
    public ServiceResult findMysql(String date, long time) {
        return null;
    }
}
