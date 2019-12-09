package com.service;

import com.util.ServiceResult;


public interface FindPeriodTimeService {
    ServiceResult findPeriod(String startDay, String endDay);
}
