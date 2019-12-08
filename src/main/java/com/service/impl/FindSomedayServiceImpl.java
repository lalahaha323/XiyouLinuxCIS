package com.service.impl;

import com.mapper.RedisMapper;
import com.service.AllUserList;
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
    private AllUserList allUserList;

    @Override
    public ServiceResult findRedis(String date, long time) {
        AllUser allUser = new AllUser();
        BinaryJedis jedis = jedisPool.getResource();
        String key;
        User[] users =  allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);
        List<User> result = new ArrayList<>();
        byte[][] keys= new byte[users.length][];
        int i = 0;
        for (User user : users) {
            key = date + ":" + user.getId();
            keys[i++] = key.getBytes();
        }

        List<byte[]> bitmaps = jedis.mget(keys);
        i = 0;
        for(byte[] bitmap : bitmaps) {
            User user = users[i++];
            List<OnOffLine> onOffLines = new ArrayList<>();
            int startIndex = 420;
            int endIndex = (int) time;
            if(bitmap == null || bitmap.length == 0)
                continue;
            BitSet bitSet = Byte2Bitset.fromByteArrayReverse(bitmap);
            int alltime = bitSet.cardinality();
            user.setAllTimeInt(alltime);
            int hour = alltime / 60;
            int minutes = alltime % 60;
            String alltimeString = (((hour == 0) ? "" : (hour + "小时")) + ((minutes == 0) ? "" : (minutes + "分钟")));
            user.setAllTimeString(alltimeString);
            while (true) {
                OnOffLine onOffLine = new OnOffLine();
                int trueIndex = bitSet.nextSetBit(startIndex);
                if (trueIndex == -1 || trueIndex > endIndex) {
                    break;
                }
                onOffLine.setOnLine(trueIndex);
                startIndex = trueIndex;
                int falseIndex = bitSet.nextClearBit(startIndex);
                if (falseIndex == -1 || falseIndex > endIndex) {
                    onOffLine.setOffLine(endIndex);
                    onOffLines.add(onOffLine);
                    break;
                }
                onOffLine.setOffLine(falseIndex);
                startIndex = falseIndex;
                onOffLines.add(onOffLine);
            }
            user.setOnOffLine(onOffLines);
            result.add(user);
        }
        jedis.close();
        result.sort((a, b) -> b.getAllTimeInt() - a.getAllTimeInt());
        allUser.setCheckInPeople(result.size());
        allUser.setNoCheckInPeople(users.length - result.size());
        allUser.setUsers(result);
        return ServiceResult.success(allUser);
    }
}
