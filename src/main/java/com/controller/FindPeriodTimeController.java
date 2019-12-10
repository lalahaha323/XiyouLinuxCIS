package com.controller;

import com.service.FindPeriodTimeService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author lala
 */

@CrossOrigin
@RestController
public class FindPeriodTimeController {

    @Autowired
    FindPeriodTimeService findPeriodTimeService;

    @GetMapping(value = "/findPeriod")
    public ServiceResult findPeriodSomeday(@RequestBody HashMap<String, String> map) {
        String startDay = map.get("startDay");
        String endDay = map.get("endDay");
        System.out.println(startDay + " " + endDay);
        return findPeriodTimeService.findPeriod(startDay, endDay);
    }
}
