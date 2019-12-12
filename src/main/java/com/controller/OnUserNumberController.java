package com.controller;

import com.service.OnUserNumberService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lala
 */

@CrossOrigin
@RestController
public class OnUserNumberController {

    @Autowired
    OnUserNumberService onUserNumberService;
    @GetMapping(value = "/onUserNumber")
    public ServiceResult onUserNumber() {
        return ServiceResult.success(onUserNumberService.onUserNumber());
    }
}
