package com.service.impl;

import com.service.FindIdFromNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lala
 */

@Service
public class FindIdFromNameServiceImpl implements FindIdFromNameService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String findIdFromName(String name) {
        Map<String, Object> user = null;
        try {
            user = this.jdbcTemplate.queryForMap("SELECT id, department FROM user WHERE name = ?", name);
        } catch (DataAccessException e) {
            return null;
        }
        return (String) user.get("id");
    }
}
