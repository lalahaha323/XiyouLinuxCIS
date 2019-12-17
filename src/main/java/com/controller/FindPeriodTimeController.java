package com.controller;

import com.config.ResultCode;
import com.service.FindPeriodTimeService;
import com.util.ServiceResult;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * 代码说明： 返回所有用户一段时间内每天的在线总时长数
 * 执行时间： 用户点击开始年月日，点击终止年月日，然后请求这个请求
 */

@CrossOrigin
@RestController
public class FindPeriodTimeController {

    @Autowired
    FindPeriodTimeService findPeriodTimeService;

    @PostMapping(value = "/findPeriod")
    public ServiceResult findPeriodSomeday(@RequestBody HashMap<String, String> map) {
        String startDay = map.get("startDay");
        String endDay = map.get("endDay");
        /** 计算今天的日期 **/
        LocalDateTime localDateTime = LocalDateTime.now();
        String nowDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime);

        /** 没有输入日期 **/
        if(startDay == null || endDay == null)
            return ServiceResult.failure(ResultCode.DATE_NO_ENTER_ERROR);
        /** 输入日期格式不正确 **/
        if((GenericValidator.isDate(startDay, "yyyyMMdd", true)) && (GenericValidator.isDate(endDay, "yyyyMMdd", true)))
            return ServiceResult.failure(ResultCode.DATE_FORMATTER_ERROR);
        /** 前端发过来的时间是今天之后的时间，还没有过 **/
        if(endDay.compareTo(nowDay) >= 0)
            return ServiceResult.failure(ResultCode.DATE_LESSTHAN_ERROR);
        return findPeriodTimeService.findPeriod(startDay, endDay);
    }
}
