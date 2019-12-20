package com.controller.all;

import com.service.OnUserNumberService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代码说明：　返回此刻在线的人数
 * 执行时间：　小组有人吗？
 */


@RestController
@RequestMapping("/all")
public class OnUserNumberController {

    @Autowired
    OnUserNumberService onUserNumberService;
    @GetMapping(value = "/onUserNumber")
    public ServiceResult onUserNumber() {
        return ServiceResult.success(onUserNumberService.onUserNumber());
    }
}
