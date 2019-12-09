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
        /**
         * 最终的返回结果
         * 包括:
         * int CheckInPeople;//总共在线人数
         * int NoCheckInPeople;//总共不在线人数
         * List<Map<String, Object>> CheckInUsers;//所有在线人数信息
         * List<Map<String, Object>> NoCheckInUsers;//所有不在线人数信息
         */
        AllUser allUser = new AllUser();
        List<Map<String, Object>> checkInUsers = new ArrayList<>();
        List<Map<String, Object>> noCheckUsers = new ArrayList<>();
        /**
         * jedis连接，获取连接池中的资源
         */
        BinaryJedis jedis = jedisPool.getResource();
        String key;
        /**
         * 所有数据库中已经有的用户user的需要信息
         * 方便快速定位到user，直接使用users[i] 这个步骤作用是将List<User>转换成User[],之后全部使用User[]
         */
        User[] users =  allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);
        /**
         * keys是所有用户的redis中存储的key值，作用是：因为jedis.mget()中参数需要传一个byte[][]
         * 构建一个key数组
         */
        byte[][] keys= new byte[users.length][];
        int i = 0;
        for (User user : users) {
            key = date + ":" + user.getId();
            keys[i++] = key.getBytes();
        }
        /**
         * 一次性获取所有用户的bitmap
         */
        List<byte[]> bitmaps = jedis.mget(keys);
        i = 0;
        for(byte[] bitmap : bitmaps) {
            /**
             * 一个登录用户的map
             * 一个未登录用户的map
             */
            Map<String, Object> checkUserMap = new HashMap<>();
            Map<String, Object> noCheckUserMap = new HashMap<>();
            User user = users[i++];
            /**
             * 一个用户一个全部上线下线时间段
             */
            List<OnOffLine> onOffLines = new ArrayList<>();
            int startIndex = 420;
            int endIndex = (int) time;

            /**
             * 用户今天没有上线
             */
            if(bitmap == null || bitmap.length == 0){
                noCheckUserMap.put("id", user.getId());
                noCheckUserMap.put("name", user.getName());
                noCheckUserMap.put("department", user.getDepartment());
                noCheckUsers.add(noCheckUserMap);
                continue;
            }
            BitSet bitSet = Byte2Bitset.fromByteArrayReverse(bitmap);
            int allTimeInt = bitSet.cardinality();
            int hour = allTimeInt / 60;
            int minutes = allTimeInt % 60;
            String alltimeString = (((hour == 0) ? "" : (hour + "小时")) + ((minutes == 0) ? "" : (minutes + "分钟")));

            /**
             * 用户今天上线的时间段统计
             */
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

            checkUserMap.put("id", user.getId());
            checkUserMap.put("name", user.getName());
            checkUserMap.put("department", user.getDepartment());

            checkUserMap.put("allTimeInt", allTimeInt);
            checkUserMap.put("allTimeString", alltimeString);
            checkUserMap.put("onOffLine", onOffLines);


            checkInUsers.add(checkUserMap);
        }
        jedis.close();
        checkInUsers.sort((a,b) -> (int)b.get("allTimeInt") - (int)a.get("allTimeInt"));
        allUser.setCheckInPeople(checkInUsers.size());
        allUser.setNoCheckInPeople(users.length - checkInUsers.size());
        allUser.setCheckInUsers(checkInUsers);
        allUser.setNoCheckInUsers(noCheckUsers);
        return ServiceResult.success(allUser);
    }
}
