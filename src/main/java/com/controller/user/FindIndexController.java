package com.controller.user;

import com.config.ResultCode;
import com.service.FindIndexService;
import com.util.ServiceResult;
import com.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 代码说明： 显示用户一个月内的时间线
 * 执行时间: 当用户登录成功之后显示的页面
 */

@CrossOrigin
@RestController
@RequestMapping("/user")
public class FindIndexController {

    @Autowired
    FindIndexService findIndexService;

    @GetMapping("/findIndex")
    public ServiceResult findIndex(HttpSession session) {
        Map<String, Object> user = (Map<String, Object>)session.getAttribute("web_user");
        if(user == null)
            return ServiceResult.failure(ResultCode.USER_NO_LOGIN);
        return findIndexService.findIndex((String) user.get("id"));
    }
}
