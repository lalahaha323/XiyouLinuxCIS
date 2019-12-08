package com.controller;

import com.service.AllUserMap;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
@RestController

public class InfoType_inController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AllUserMap allUserMap;

    @PostMapping("/getInfo")
    public ServiceResult getInfo(HttpSession session){
        Map user = (Map)session.getAttribute("user");
        if(user == null){
            return ServiceResult.failure("403", "未登录");
        }
        String userId = (String) user.get("userId");
        Map db_user = jdbcTemplate.queryForMap("SELECT * FROM user WHERE id = ?", userId);
        if(db_user == null){
            return ServiceResult.success(user);
        }
        return ServiceResult.success(db_user);
    }

    /**
     * 更改用户mac地址/登记自己的mac地址
     * @param formData
     * @param session
     * @return
     */
    @PostMapping("submit")
    public ServiceResult submit(@RequestBody Map formData, HttpSession session){
        Map user = (Map)session.getAttribute("user");
        if(user == null){
            return ServiceResult.failure("300", "请重新进入小程序");
        }
        Map systemInfo = (Map)formData.get("systemInfo");
        String userId = (String) user.get("userId");
        Map db_user = null;
        try {
            db_user = jdbcTemplate.queryForMap("SELECT * FROM user WHERE id = ?", userId);
            Timestamp modify_time = (Timestamp) db_user.get("modify_time");
            long day = (modify_time.getTime() / 1000 + 60 * 60 * 24 * 60 - new Date().getTime() / 1000) / (60 * 60 * 24);
            if(day > 0){
                return ServiceResult.failure("300", "请在" + (day + 1) + "天后再修改");
            }
            int result = jdbcTemplate.update("UPDATE user SET name=?, department = ?, mac=?, brand=?, model=?, platform=?, system=?, isAdmin = ? WHERE id=?",
                    user.get("userName"),
                    user.get("userDepartment"),
                    formData.get("mac").toString().toLowerCase(),
                    systemInfo.get("brand"),
                    systemInfo.get("model"),
                    systemInfo.get("platform"),
                    systemInfo.get("system"),
                    user.get("userIsAdmin"),
                    userId
            );
            if(result < 0){
                return ServiceResult.failure("500", "系统错误");
            }
        }catch (EmptyResultDataAccessException e){
            Long userDepartment = null;
            System.out.println(user.get("userDepartment"));
            try {
                userDepartment = ((List<Long>)user.get("userDepartment")).get(0);
            } catch (IndexOutOfBoundsException ex) {
                ;
            }
            int result = jdbcTemplate.update("INSERT INTO user VALUES(?,?,?,?,?,?,?,?,?,?)",
                    userId,
                    user.get("userName"),
                    userDepartment,
                    formData.get("mac").toString().toLowerCase(),
                    systemInfo.get("brand"),
                    systemInfo.get("model"),
                    systemInfo.get("platform"),
                    systemInfo.get("system"),
                    user.get("userIsAdmin"),
                    null
            );
            if(result < 0){
                return ServiceResult.failure("500", "系统错误");
            }
        }
        allUserMap.initMap();
        return ServiceResult.success(null);
    }
}
