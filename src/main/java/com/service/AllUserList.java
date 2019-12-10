package com.service;

import com.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lala
 */

/**
 * 为了从数据库中获取所有的id，name，department，mac保存在内存中的List<User> allUserList之中
 * 相当与以下例子：
 * User1里面有id1,name1,department1,mac1
 * User2里面有id2,name2,department2,mac2
 * ......
 */
@Service
public class AllUserList {
    public List<User> allUserList = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;

    public void initMap(){
        allUserList.clear();
        List<Map<String, Object>> db_users = this.jdbcTemplate.queryForList("SELECT id,name,department,mac FROM user");
        for (Map userMap : db_users){
            User user = new User();
            user.setId((String) userMap.get("id"));
            user.setName((String) userMap.get("name"));
            user.setDepartment((Integer) userMap.get("department"));
            user.setMac((String) userMap.get("mac"));
            allUserList.add(user);
        }
    }

    @Autowired
    public AllUserList(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.initMap();
    }
}
