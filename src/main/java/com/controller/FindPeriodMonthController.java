package com.controller;

import com.config.ResultCode;
import com.service.FindPeriodMonthService;
import com.util.ServiceResult;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * 代码说明: 返回所有用户一段时间的每月统计时间
 * 执行时间： 当管理员输入开始月份，和截止月份的时候，出发这个请求
 */

@CrossOrigin
@RestController
public class FindPeriodMonthController {

    @Autowired
    FindPeriodMonthService findPeriodMonthService;

    @PostMapping(value = "/findPeriodMonth")
    public ServiceResult findPeriodMonth(@RequestBody HashMap<String, String> map) {
        String startMonth = map.get("startMonth");
        String endMonth = map.get("endMonth");
        /** 没有输入日期 **/
        if(startMonth == null || endMonth == null)
            return ServiceResult.failure(ResultCode.DATE_NO_ENTER_ERROR);
        /** 输入格式不正确 **/
        if(!GenericValidator.isDate(startMonth+"01", "yyyyMMdd", true))
            return ServiceResult.failure(ResultCode.DATE_FORMATTER_ERROR);
        if(!GenericValidator.isDate(endMonth+"01", "yyyyMMdd", true))
            return ServiceResult.failure(ResultCode.DATE_FORMATTER_ERROR);
        /** 还没到你输入的月份 **/
        LocalDateTime localDateTime = LocalDateTime.now();
        String nowDay = DateTimeFormatter.ofPattern("yyyyMM").format(localDateTime);
        if(endMonth.compareTo(nowDay) > 0)
            return ServiceResult.failure(ResultCode.DATE_LESSTHAN_MONTH_ERROR);
        return findPeriodMonthService.findPeriodMonth(startMonth, endMonth);
    }
}
