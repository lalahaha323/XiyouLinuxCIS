package com.service.impl;

import com.mapper.RedisMapper;
import com.service.AllUserMap;
import com.service.FindSomedayService;
import com.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryJedis;
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
        AllUser allUser = new AllUser();
        BinaryJedis jedis = jedisPool.getResource();
        String key;
        UserUtil[] userUtils =  allUserMap.allUserMap.values().toArray(new UserUtil[0]);
        List<UserUtil> result = new ArrayList<>();
        byte[][] keys= new byte[userUtils.length][];
        int i = 0;
        for (UserUtil userUtil : userUtils) {
            key = date + ":" + userUtil.getId();
            keys[i++] = key.getBytes();
        }

        List<byte[]> bitmaps = jedis.mget(keys);
        i = 0;
        for(byte[] bitmap : bitmaps) {
            UserUtil userUtil = userUtils[i++];
            List<OnOffLineUtil> onOffLineUtils = new ArrayList<>();
            int startIndex = 420;
            int endIndex = (int) time;
            if(bitmap == null || bitmap.length == 0)
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
            result.add(userUtil);
        }
        jedis.close();
        result.sort((a, b) -> b.getAllTimeInt() - a.getAllTimeInt());
        allUser.setCheckInPeople(result.size());
        allUser.setNoCheckInPeople(userUtils.length - result.size());
        allUser.setUserUtils(result);
        return ServiceResult.success(allUser);
    }
}
