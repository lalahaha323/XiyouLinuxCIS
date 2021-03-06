package com.controller.all;

import com.config.ResultCode;
import com.service.FindIndexService;
import com.util.ServiceResult;
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


@RestController
@RequestMapping("/all")
public class FindIndexController {

    @Autowired
    FindIndexService findIndexService;

    @GetMapping(value = "/findIndex" )
    public ServiceResult findIndex(HttpSession session) {
        Map<String, Object> user = (Map<String, Object>)session.getAttribute("web_user");
        if(user == null)
            return ServiceResult.failure(ResultCode.USER_NO_LOGIN_ERROR);
        return findIndexService.findIndex((String) user.get("id"));
    }
}
