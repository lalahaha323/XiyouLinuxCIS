package com.service.impl;

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
    JedisPool jedisPool;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private AllUserList allUserList;

    @Override
    public ServiceResult findRedis(String date, long time) {
        //最后返回的所有结果
        AllUser allUser = new AllUser();
        List<Map<String, Object>> allUsers = new ArrayList<>();
        //jedis连接
        BinaryJedis jedis = jedisPool.getResource();
        String key;

        //方便快速定位到user，直接使用users[i] 这个步骤作用是将List<User>转换成User[],之后全部使用User[]
        User[] users =  allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);

//        List<User> result = new ArrayList<>();
        //keys是所有用户的redis中存储的key值，作用是：因为jedis.mget()中参数需要传一个byte[][]
        byte[][] keys= new byte[users.length][];
        int i = 0;
        for (User user : users) {
            key = date + ":" + user.getId();
            keys[i++] = key.getBytes();
        }
        //一次性获取所有用户的bitmap
        List<byte[]> bitmaps = jedis.mget(keys);
        i = 0;
        for(byte[] bitmap : bitmaps) {
            //一个用户一个resultMap
            Map<String, Object> resultMap = new HashMap<>();
            //获取第一个user
            User user = users[i++];
            //之后一个用户一个全部上线下线时间段
            List<OnOffLine> onOffLines = new ArrayList<>();

            //起止时间
            int startIndex = 420;
            int endIndex = (int) time;

            //作用：用户今天没有上线
            if(bitmap == null || bitmap.length == 0)
                continue;
            BitSet bitSet = Byte2Bitset.fromByteArrayReverse(bitmap);
            int allTimeInt = bitSet.cardinality();

            int hour = allTimeInt / 60;
            int minutes = allTimeInt % 60;
            String alltimeString = (((hour == 0) ? "" : (hour + "小时")) + ((minutes == 0) ? "" : (minutes + "分钟")));

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

            resultMap.put("id", user.getId());
            resultMap.put("name", user.getName());
            resultMap.put("department", user.getDepartment());

            resultMap.put("allTimeInt", allTimeInt);
            resultMap.put("allTimeString", alltimeString);
            resultMap.put("onOffLine", onOffLines);

            allUsers.add(resultMap);
        }
        jedis.close();
        allUsers.sort((a,b) -> (int)b.get("allTimeInt") - (int)a.get("allTimeInt"));
        allUser.setCheckInPeople(allUsers.size());
        allUser.setNoCheckInPeople(users.length - allUsers.size());
        allUser.setUsers(allUsers);
        return ServiceResult.success(allUser);
    }
}
