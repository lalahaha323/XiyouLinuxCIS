package com.service.impl;

import com.service.AllUserList;
import com.service.FindPeriodMonthService;
import com.util.ServiceResult;
import com.util.Time;
import com.util.User;
import com.util.UserTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 * 不包括今天的在线时间
 */

@Slf4j
@Service
public class FindPeriodMonthServiceImpl implements FindPeriodMonthService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AllUserList allUserList;

    @Override
    public ServiceResult findPeriodMonth(String startMonth, String endMonth) {
        /**
         * 计算今天的年月
         */
        LocalDateTime localDateTime = LocalDateTime.now();
        String nowDay = DateTimeFormatter.ofPattern("yyyyMM").format(localDateTime);

        /**
         * 前端发过来的时间是今天之后的时间，还没有过
         */
        if(endMonth.compareTo(nowDay) > 0)
            return ServiceResult.failure("410", "还没有到你输入的月份哦～");

        /**
         * 最后返回的结果，从数据库中查每个人每天在线的总时长，然后一个月的加到一起
         */
        Map<String, UserTime> allUserTime = new HashMap<>();
        User[] users = allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);
        for(User user : users) {
            UserTime userTime = new UserTime();
            userTime.setName(user.getName());
            List<Time> timeList = new ArrayList<>();
            userTime.setTimeList(timeList);
            allUserTime.put(user.getId(), userTime);
        }

        /**
         * 如果对方要查3月到12月的记录，那么就相当于查询20190301-20191231的记录
         */
        startMonth += "01";
        int year = Integer.parseInt(endMonth.substring(0, 4));
        int month = Integer.parseInt(endMonth.substring(4, 6));
        int day = 1;
        LocalDate localDate = LocalDate.of(year, month, day);
        endMonth += localDate.lengthOfMonth();

        /**
         * format是为了
         * format2是为了方便String转换Date用
         */
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
        /**
         * 从数据库中查找
         */
        List<Map<String, Object>> db_users = jdbcTemplate.queryForList("SELECT id, timeday, alltime FROM daytime WHERE timeday BETWEEN  ? AND  ? ", startMonth, endMonth);
        for(String id : allUserTime.keySet()) {
            int j = 0;
            int i = 0;
            for(Map userMap : db_users) {
                /**
                 * 找到对应的那个id UserTime
                 */
                if(userMap.get("id").equals(id)) {
                    /**
                     * 说明是第一次，里面没有数据
                     */
                    if(i == 0)
                    {
                        Time time = new Time();

                        time.setAllTime((int) userMap.get("alltime"));
                        String egDay = format2.format((Date) userMap.get("timeday"))+ "-01";
                        time.setTimeDay(Date.valueOf(egDay));
                        allUserTime.get(id).getTimeList().add(time);
                        i = 1;
                        j++;
                    } else {
                        /**
                         * 取得上一个存进去的是几月份的
                         */
                        Date timeBefore = allUserTime.get(id).getTimeList().get(j-1).getTimeDay();
                        Date timeAfter = (Date) userMap.get("timeday");

                        String timeStringBefore = format.format(timeBefore);
                        String timeStringAfter = format.format(timeAfter);

                        /**
                         * 如果这次的和上次是一月，则时间加到一起就行
                         */
                        if(timeStringBefore.equals(timeStringAfter)){
                            int time = allUserTime.get(id).getTimeList().get(j-1).getAllTime() + (int) userMap.get("alltime");
                            allUserTime.get(id).getTimeList().get(j-1).setAllTime(time);
                        } else {
                            Time time = new Time();
                            time.setAllTime((int) userMap.get("alltime"));
                            String egDay = format2.format((Date) userMap.get("timeday"))+ "-01";
                            time.setTimeDay(Date.valueOf(egDay));
                            allUserTime.get(id).getTimeList().add(time);
                            j++;
                        }
                    }
                }
            }
        }
        return ServiceResult.success(allUserTime.values());
    }
}
