package com.service.impl;

import com.service.FindUserPeriodMonthService;
import com.util.ServiceResult;
import com.util.Time;
import com.util.UserTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */

@Slf4j
@Service
public class FindUserPeriodMonthServiceImpl implements FindUserPeriodMonthService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResult findUSerPeriodMonth(String startMonth, String endMonth, String id) {

        /** 最后返回的结构 **/
        UserTime userTime = new UserTime();
        List<Time> timeList = new ArrayList<>();
        userTime.setTimeList(timeList);

        /** 如果对方要查3月到12月的记录，那么就相当于查询20190301-20191231的记录 **/
        startMonth += "01";
        int year = Integer.parseInt(endMonth.substring(0, 4));
        int month = Integer.parseInt(endMonth.substring(4, 6));
        int day = 1;
        LocalDate localDate = LocalDate.of(year, month, day);
        endMonth += localDate.lengthOfMonth();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");

        List<Map<String, Object>> db_users = jdbcTemplate.queryForList("SELECT timeday, alltime FROM daytime WHERE id= ? AND timeday BETWEEN ? AND ?", id, startMonth, endMonth);
        Map<String, Object> db_user = jdbcTemplate.queryForMap("SELECT name FROM user WHERE id = ?", id);
        String name = (String) db_user.get("name");
        userTime.setName(name);


        int j = 0;
        int i = 0;
        for(Map userMap : db_users) {
            /** 说明是第一次，里面没有数据 **/
            if(i == 0) {
                Time time = new Time();

                time.setAllTime((int) userMap.get("alltime"));
                String egDay = format2.format((Date) userMap.get("timeday"))+ "-01";
                time.setTimeDay(Date.valueOf(egDay));
                userTime.getTimeList().add(time);
                i = 1;
                j++;
            } else {
                /** 取得上一个存进去的是几月份的 **/
                Date timeBefore = userTime.getTimeList().get(j-1).getTimeDay();
                Date timeAfter = (Date) userMap.get("timeday");

                String timeStringBefore = format.format(timeBefore);
                String timeStringAfter = format.format(timeAfter);

                /** 如果这次的和上次是一月，则时间加到一起就行 **/
                if(timeStringBefore.equals(timeStringAfter)){
                    int time = userTime.getTimeList().get(j-1).getAllTime() + (int) userMap.get("alltime");
                    userTime.getTimeList().get(j-1).setAllTime(time);
                } else {
                    Time time = new Time();
                    time.setAllTime((int) userMap.get("alltime"));
                    String egDay = format2.format((Date) userMap.get("timeday"))+ "-01";
                    time.setTimeDay(Date.valueOf(egDay));
                    userTime.getTimeList().add(time);
                    j++;
                }
            }
        }


        return ServiceResult.success(userTime);
    }
}
