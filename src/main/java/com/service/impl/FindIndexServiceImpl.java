package com.service.impl;

import com.config.ResultCode;
import com.service.FindIndexService;
import com.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */
@Slf4j
@Service
public class FindIndexServiceImpl implements FindIndexService {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResult findIndex(String id) {
        /** 如果是今天的在线时长，需要计算此刻距离0点是几分钟 **/
        LocalDateTime nowTime = LocalDateTime.now();
        int allMinutes = nowTime.getHour() * 60 + nowTime.getMinute();

        /** 主要看上个月的x号到这个月的x号相差几天 **/
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();
        LocalDate last = now.minus(1, ChronoUnit.MONTHS);
        long periodDays = ChronoUnit.DAYS.between(last, now);

        /** 返回的最终结果 **/
        UserSingle userSingle = new UserSingle();
        List<TimeSingle> timeSingles = new ArrayList<>();

        /** 从数据库中去查找 **/
        try {
            Map<String, Object> db_user = jdbcTemplate.queryForMap("SELECT department,name FROM user WHERE id = ?", id);
            int userDepartment = Integer.parseInt(db_user.get("department").toString());
            String name = (String) db_user.get("name");
            userSingle.setName(name);
            userSingle.setDepartment(userDepartment);
            userSingle.setTimeSingles(timeSingles);
        } catch (EmptyResultDataAccessException e) {
            /** 数据库中没有这个用户 **/
            e.printStackTrace();
            return ServiceResult.failure(ResultCode.USER_NO_ERROR);
        }

        /** 从jedis中去查找 **/
        BinaryJedis jedis = jedisPool.getResource();
        byte[][] keys = new byte[(int) (periodDays+1)][];
        int i = (int)periodDays + 1;
        while(i > 0) {
            /** 昨天 = 今天 - 1 **/
            LocalDate last2 = now.minus(1, ChronoUnit.DAYS);
            TimeSingle timeSingle = new TimeSingle();
            timeSingle.setDate(now.format(formatter));
            timeSingles.add(timeSingle);
            String key = now.format(formatter) + ":" + id;
            keys[(int)periodDays + 1 - i] = key.getBytes();
            i--;
            now = last2;
        }
        userSingle.setTimeSingles(timeSingles);

        /** 一次性获取所有用户的bitmap **/
        i = 0;
        List<byte[]> bitmaps = jedis.mget(keys);
        jedis.close();
        for(byte[] bitmap : bitmaps) {
            List<OnOffLine> onOffLines = new ArrayList<>();
            int startIndex = 420;
            int endIndex;
            if(i == 0)
                endIndex = allMinutes;
            else
                endIndex = 1440;
            if(bitmap == null || bitmap.length == 0){
                userSingle.getTimeSingles().get(i).setAllTimeString("");
                userSingle.getTimeSingles().get(i).setOnOffLines(onOffLines);
                i++;
                continue;
            }

            BitSet bitSet = Byte2Bitset.fromByteArrayReverse(bitmap);
            int allTimeInt = bitSet.cardinality();
            int hour = allTimeInt / 60;
            int minutes = allTimeInt % 60;
            String alltimeString = (((hour == 0) ? "" : (hour + "小时")) + ((minutes == 0) ? "" : (minutes + "分钟")));

            /** 用户今天上线的时间段统计 **/
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
                    break;
                }
                onOffLine.setOffLine(falseIndex);
                startIndex = falseIndex;
                onOffLines.add(onOffLine);
                userSingle.getTimeSingles().get(i).setAllTimeString(alltimeString);
                userSingle.getTimeSingles().get(i).setOnOffLines(onOffLines);
            }
            i++;
        }
        return ServiceResult.success(userSingle);
    }
}
