package com.controller;

import com.util.ServiceResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

/**
 * @author lala
 */
@CrossOrigin
@RestController
public class CurrentTimeController {

    @GetMapping(value = "/getCurrentTime")
    public ServiceResult getCurrentTime() {
        return ServiceResult.success(System.currentTimeMillis());
    }
}
