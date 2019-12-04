package com.controller;

import com.mapper.RedisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lala
 */
@RestController
public class FindSomedayController {

    @Autowired
    private RedisMapper redisMapper;

}
