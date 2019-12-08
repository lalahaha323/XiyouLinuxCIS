package com.service;

import com.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */

/**
 * 为了从数据库中获取所有的id，name，department，mac保存在内存中的HashMap<String, UserUtil>之中
 * 相当与以下例子：
 * mac1 : UserUtil1
 * mac2 : UserUtil2
 * mac3 : UserUtil3
 * ......
 */
@Service
public class AllUserMap {
    public HashMap<String, User> allUserMap = new HashMap<>();
    private JdbcTemplate jdbcTemplate;

    public void initMap(){
        List<Map<String, Object>> db_users = this.jdbcTemplate.queryForList("SELECT id,name,department,mac FROM user");
        for (Map user : db_users){
            User userUtil = new User();
            userUtil.setId((String) user.get("id"));
            userUtil.setName((String) user.get("name"));
            userUtil.setDepartment((Integer) user.get("department"));
            allUserMap.put((String) user.get("mac"), userUtil);
        }
    }

    @Autowired
    public AllUserMap(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.initMap();
    }
}
