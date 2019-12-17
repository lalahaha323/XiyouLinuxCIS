package com.controller;

import com.config.ResultCode;
import com.service.FindSomedayService;
import com.util.ServiceResult;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 代码说明： 返回某一天所有用户的在线时间轴
 * 执行时间： 管理员登录之后的初始化页面，默认显示今天所有同学的在线时间轴，也可以选择时间显示所有同学这个时间的在线时间轴
 */

@CrossOrigin
@RestController
public class FindSomedayController {

    @Autowired
    FindSomedayService findSomedayService;

    @GetMapping(value = "/FindSomeday")
    public ServiceResult findSomeday(@Param("date") String date) {
        /** 没有输入日期 **/
        if(date == null)
            return ServiceResult.failure(ResultCode.DATE_NO_ENTER_ERROR);
        /** 输入日期格式不正确 **/
        if(GenericValidator.isDate(date, "yyyyMMdd", true))
            return ServiceResult.failure(ResultCode.DATE_FORMATTER_ERROR);
        LocalDateTime localDateTime = LocalDateTime.now();
        String dateNow = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime);
        if (date.equals(dateNow)) {

            /** 要查找今天的在线时间轴，截止时间为此时此刻 **/
            long time = localDateTime.getHour() * 60 + localDateTime.getMinute();
            return ServiceResult.success(findSomedayService.findRedis(date, time));
        } else {

            /** 要查找之前的在线时间轴，截止时间为最后一刻 **/
            return ServiceResult.success(findSomedayService.findRedis(date, 1440));
        }
    }
}

