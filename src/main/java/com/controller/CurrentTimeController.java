package com.controller;

import com.util.ServiceResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 代码说明：　返回此刻的时间戳
 */
@CrossOrigin
@RestController
public class CurrentTimeController {

    @GetMapping(value = "/getCurrentTime")
    public ServiceResult getCurrentTime() {
        return ServiceResult.success(System.currentTimeMillis());
    }
}
