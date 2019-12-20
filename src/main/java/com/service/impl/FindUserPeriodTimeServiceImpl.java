package com.service.impl;

import com.service.FindUserPeriodTimeService;
import com.util.ServiceResult;
import com.util.Time;
import com.util.UserTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */

@Slf4j
@Service
public class FindUserPeriodTimeServiceImpl implements FindUserPeriodTimeService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResult findUserPeriodTime(String startDay, String endDay, String id) {

        /** 最后返回的结果 **/
        UserTime userTime = new UserTime();
        List<Time> timeList = new ArrayList<>();
        userTime.setTimeList(timeList);

        List<Map<String, Object>> db_user_daytime = jdbcTemplate.queryForList("SELECT timeday, alltime FROM daytime WHERE id = ? AND timeday BETWEEN ? AND ?", id, startDay, endDay);
        Map<String, Object> db_user = jdbcTemplate.queryForMap("SELECT name FROM user WHERE id = ?", id);
        String name = (String) db_user.get("name");
        userTime.setName(name);



        for(Map userMap : db_user_daytime) {
            Time time = new Time();
            time.setAllTime((int) userMap.get("alltime"));
            time.setTimeDay((Date) userMap.get("timeday"));
            timeList.add(time);
        }


        return ServiceResult.success(userTime);
    }
}
