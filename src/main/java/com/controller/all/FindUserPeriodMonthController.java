package com.controller.all;

import com.config.ResultCode;
import com.service.FindUserPeriodMonthService;
import com.util.ServiceResult;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lala
 */

@RestController
@RequestMapping("/all")
public class FindUserPeriodMonthController {

    @Autowired
    FindUserPeriodMonthService findUserPeriodMonthService;

    @PostMapping(value = "/findPeriodMonth")
    public ServiceResult findUserPeriodMonth(@RequestBody HashMap<String, String> map, HttpSession httpSession) {
        String startMonth = map.get("startMonth");
        String endMonth = map.get("endMonth");
        Map user = (Map)httpSession.getAttribute("web_user");
        String id = (String) user.get("id");

        /** 没有输入用户id **/
        if(id == null)
            return ServiceResult.failure(ResultCode.USER_NO_ID_ERROR);
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
        return findUserPeriodMonthService.findUSerPeriodMonth(startMonth, endMonth, id);
    }
}
