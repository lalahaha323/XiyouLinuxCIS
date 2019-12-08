package com.service;

import com.util.ServiceResult;

/**
 * @author lala
 */

public interface FindSomedayService {
    public ServiceResult findRedis(String date, long time);
    public ServiceResult findMysql(String date, long time);
}
