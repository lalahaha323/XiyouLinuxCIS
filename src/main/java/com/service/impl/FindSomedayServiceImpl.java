package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapper.RedisMapper;
import com.service.AllUserMap;
import com.service.FindSomedayService;
import com.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * @author lala
 */
@Slf4j
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
        String key;
        List<UserUtil> userUtils = new ArrayList<>();
        for (UserUtil userUtil : allUserMap.allUserMap.values()) {
            List<OnOffLineUtil> onOffLineUtils = new ArrayList<>();
            key = date + ":" + userUtil.getId();
            /**
             * 说明在redis数据库中有这个，此刻他来签到了
             */
            int startIndex = 420;
            int endIndex = (int) time;
            byte[] bitmap = jedis.get(key.getBytes());
            if (bitmap == null || bitmap.length == 0)
                continue;
            BitSet bitSet = Byte2Bitset.fromByteArrayReverse(bitmap);
            int alltime = bitSet.cardinality();
            userUtil.setAllTimeInt(alltime);
            int hour = alltime / 60;
            int minutes = alltime % 60;
            String alltimeString = (((hour == 0) ? "" : (hour + "小时")) + ((minutes == 0) ? "" : (minutes + "分钟")));
            userUtil.setAllTimeString(alltimeString);
            while (true) {
                OnOffLineUtil onOffLineUtil = new OnOffLineUtil();
                int trueIndex = bitSet.nextSetBit(startIndex);
                if (trueIndex == -1 || trueIndex > endIndex) {
                    break;
                }
                onOffLineUtil.setOnLine(trueIndex);
                startIndex = trueIndex;
                int falseIndex = bitSet.nextClearBit(startIndex);
                if (falseIndex == -1 || falseIndex > endIndex) {
                    onOffLineUtil.setOffLine(endIndex);
                    onOffLineUtils.add(onOffLineUtil);
                    break;
                }
                onOffLineUtil.setOffLine(falseIndex);
                startIndex = falseIndex;
                onOffLineUtils.add(onOffLineUtil);
            }
            userUtil.setOnOffLineUtils(onOffLineUtils);
            userUtils.add(userUtil);
        }
        jedis.close();
        return ServiceResult.success(sortTime(userUtils));
    }

    @Override
    public ServiceResult findMysql(String date, long time) {
        return null;
    }

    public List<UserUtil> sortTime(List<UserUtil> userUtils) {
        Collections.sort(userUtils);
        return userUtils;
    }
}
