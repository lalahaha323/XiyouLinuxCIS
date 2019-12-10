package com.controller;

import com.service.FindPeriodMonthService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author lala
 */

@CrossOrigin
@RestController
public class FindPeriodMonthController {

    @Autowired
    FindPeriodMonthService findPeriodMonthService;

    @GetMapping(value = "/findPeriodMonth")
    public ServiceResult findPeriodMonth(@RequestBody HashMap<String, String> map) {
        String startMonth = map.get("startMonth");
        String endMonth = map.get("endMonth");
        if(startMonth == null || endMonth == null)
            return ServiceResult.failure("777", "你参数输入错误");
        return findPeriodMonthService.findPeriodMonth(startMonth, endMonth);
    }
}
