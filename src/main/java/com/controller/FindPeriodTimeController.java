package com.controller;

import com.service.Redis2MysqlTask;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lala
 */

@CrossOrigin
@RestController
public class FindPeriodTimeController {

    @Autowired
    Redis2MysqlTask redis2MysqlTask;

    @ResponseBody
    @GetMapping(value = "/FindSomeday2")
    public ServiceResult findSomeday() {
        return null;
    }
}
