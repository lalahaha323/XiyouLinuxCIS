package com.service.impl;

import com.service.AllUserList;
import com.service.FindPeriodTimeService;
import com.util.ServiceResult;
import com.util.Time;
import com.util.User;
import com.util.UserTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */
@Slf4j
@Service
public class FindPeriodTimeServiceImpl implements FindPeriodTimeService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AllUserList allUserList;

    @Override
    public ServiceResult findPeriod(String startDay, String endDay) {

        /** 最后返回的结果,从数据库中查每个人每天在线的总时长 **/
        Map<String, UserTime> allUserTime = new HashMap<>();
        User[] users =  allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);
        for (User user : users) {
            UserTime userTime = new UserTime();
            userTime.setName(user.getName());
            List<Time> timeList = new ArrayList<>();
            userTime.setTimeList(timeList);
            allUserTime.put(user.getId(),userTime);
        }
        /**
         * 从数据库中查找
         */
        List<Map<String, Object>> db_users = jdbcTemplate.queryForList("SELECT id, timeday, alltime FROM daytime WHERE timeday BETWEEN  ? AND  ? ", startDay, endDay);
        for(String id : allUserTime.keySet()) {
            for(Map userMap : db_users) {
                if(userMap.get("id").equals(id)) {
                    Time time = new Time();
                    time.setAllTime((int) userMap.get("alltime"));
                    time.setTimeDay((Date) userMap.get("timeday"));
                    allUserTime.get(id).getTimeList().add(time);
                }
            }
        }
        return ServiceResult.success(allUserTime.values());
    }
}
