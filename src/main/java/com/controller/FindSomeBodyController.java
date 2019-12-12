package com.controller;

import com.service.FindSomeBodyService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lala
 */

@CrossOrigin
@RestController
public class FindSomeBodyController {

    @Autowired
    FindSomeBodyService findSomeBodyService;

    @GetMapping(value = "/findSomeBody")
    public ServiceResult findSomeBody(@Param("name") String name) {
        return findSomeBodyService.findSomeBody(name);
    }
}
