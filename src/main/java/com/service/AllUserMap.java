package com.service;

import com.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */

@Service
public class AllUserMap {
    public HashMap<String, UserUtil> allUserMap = new HashMap<>();

    @Autowired
    public AllUserMap(JdbcTemplate jdbcTemplate){
        List<Map<String, Object>> db_users = jdbcTemplate.queryForList("SELECT id,name,department,mac FROM user");
        for (Map user : db_users){
            UserUtil userUtil = new UserUtil();
            userUtil.setId((String) user.get("id"));
            userUtil.setName((String) user.get("name"));
            userUtil.setDepartment((Integer) user.get("department"));
            allUserMap.put((String) user.get("mac"), userUtil);
        }
    }
}
