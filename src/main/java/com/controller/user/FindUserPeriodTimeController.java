package com.controller.user;

import com.config.ResultCode;
import com.service.FindUserPeriodTimeService;
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
@RequestMapping("/user")
public class FindUserPeriodTimeController {

    @Autowired
    FindUserPeriodTimeService findUserPeriodTimeService;

    @PostMapping(value = "/findPeriod")
    public ServiceResult findUserPeriodTime(@RequestBody HashMap<String, String> map, HttpSession httpSession) {
        String startDay = map.get("startDay");
        String endDay = map.get("endDay");
        Map user = (Map)httpSession.getAttribute("web_user");
        String id = (String) user.get("id");
        /** 计算今天的日期 **/
        LocalDateTime localDateTime = LocalDateTime.now();
        String nowDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDateTime);

        /** 没有输入用户id **/
        if(id == null)
            return ServiceResult.failure(ResultCode.USER_NO_ID_ERROR);
        /** 没有输入日期 **/
        if(startDay == null || endDay == null)
            return ServiceResult.failure(ResultCode.DATE_NO_ENTER_ERROR);
        /** 输入日期格式不正确 **/
        if(! GenericValidator.isDate(startDay, "yyyyMMdd", true))
            return ServiceResult.failure(ResultCode.DATE_FORMATTER_ERROR);
        if(! GenericValidator.isDate(endDay, "yyyyMMdd", true))
            return ServiceResult.failure(ResultCode.DATE_FORMATTER_ERROR);
        /** 前端发过来的时间是今天之后的时间，还没有过 **/
        if(endDay.compareTo(nowDay) >= 0)
            return ServiceResult.failure(ResultCode.DATE_LESSTHAN_ERROR);
        return findUserPeriodTimeService.findUserPeriodTime(startDay, endDay, id);
    }
}
