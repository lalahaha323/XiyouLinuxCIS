package com.controller.other;

import com.config.ResultCode;
import com.service.AllUserList;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 代码说明： 更改用户mac地址/登记自己的mac地址
 * 发生时间： 当用户提交mac地址的时候[就是用户填写完mac地址之后，点击提交，前端就会给后台发送这个请求]
 */
@RestController
@RequestMapping("/other")
public class CheckUpdateMacController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AllUserList allUserList;

    @PostMapping("submit")
    public ServiceResult submit(@RequestBody Map formData, HttpSession session){
        Map user = (Map)session.getAttribute("user");
        if(user == null){

            /** 时间超时请重新登录[意思就是用户需要退出重新进入小程序中] **/
            return ServiceResult.failure(ResultCode.TIME_OBSOLETE_ERROR);

        }
        Map systemInfo = (Map)formData.get("systemInfo");
        String userId = (String) user.get("userId");
        Map db_user = null;
        try {

            /** 更改mac地址[说明这个用户是来更新mac地址的] **/
            db_user = jdbcTemplate.queryForMap("SELECT * FROM user WHERE id = ?", userId);
            Timestamp modify_time = (Timestamp) db_user.get("modify_time");
            long day = (modify_time.getTime() / 1000 + 60 * 60 * 24 * 60 - new Date().getTime() / 1000) / (60 * 60 * 24);
            if(day > 0){

                /** 保证60天内不能修改mac地址 **/
                ResultCode.TIME_LESSTHAN_ERROE.setMessgae("请在" + (day + 1) + "天后再修改");
                return ServiceResult.failure(ResultCode.TIME_LESSTHAN_ERROE);
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
                return ServiceResult.failure(ResultCode.SQL_EXECUTE_ERROE);
            }
        }catch (EmptyResultDataAccessException e){
            /** 登记mac地址[说明这个用户是为了登记mac地址的] **/
            Long userDepartment = null;
            try {
                userDepartment = ((List<Long>)user.get("userDepartment")).get(0);
            } catch (IndexOutOfBoundsException ex) {
                ;
            }
            int result = jdbcTemplate.update("INSERT INTO user (id, name, department, mac, brand, model, platform, system, isAdmin, modify_time) VALUES(?,?,?,?,?,?,?,?,?,?)",
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
                return ServiceResult.failure(ResultCode.SQL_EXECUTE_ERROE);
            }
        }
        allUserList.initMap();
        return ServiceResult.success(null);
    }
}
