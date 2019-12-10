package com.controller;

import com.service.FindSomedayService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lala
 */
@CrossOrigin
@RestController
public class FindSomedayController {

    @Autowired
    FindSomedayService findSomedayService;

    @ResponseBody
    @GetMapping(value = "/FindSomeday")
    public ServiceResult findSomeday(@Param("date") String date) {
        if(date == null)
            return ServiceResult.failure("999", "没有输入日期");
        LocalDateTime localDateTime = LocalDateTime.now();
        String dateNow = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime);
        long time = localDateTime.getHour() * 60 + localDateTime.getMinute();
        if (date.equals(dateNow)) {
            /**
             * 从redis中去查找，调用findSomedayService中的findRedis方法
             */
            return findSomedayService.findRedis(date, time);
        } else {
            /**
             * 从MySQL中去查找，调用findSomedayService中的findMysql方法
             */
            return findSomedayService.findRedis(date, 1440);
        }
    }
}

