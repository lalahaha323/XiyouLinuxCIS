package com.controller;

import com.service.EarlyRankService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代码说明：　返回今天的早到排名
 * 执行时间：　想要查看某天早到排名情况
 */
@CrossOrigin
@RestController
public class EarlyRankController {

    @Autowired
    EarlyRankService earlyRankService;

    @GetMapping(value = "/early")
    public ServiceResult earlyRank(@Param("date") String date) {
        return earlyRankService.earlyRank(date);
    }
}
