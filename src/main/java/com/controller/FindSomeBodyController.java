package com.controller;

import com.service.FindSomeBodyService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 代码说明：　返回这个用户近一个月的时间线
 * 执行时间：　想要查看近一个月某人的每天的时间线
 */

@CrossOrigin
@RestController
public class FindSomeBodyController {

    @Autowired
    FindSomeBodyService findSomeBodyService;

    @GetMapping(value = "/findSomeBody")
    public ServiceResult findSomeBody(@Param("id") String id) {
        return findSomeBodyService.findSomeBody(id);
    }
}
