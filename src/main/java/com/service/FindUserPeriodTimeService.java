package com.service;

import com.util.ServiceResult;

/**
 * @author lala
 */
public interface FindUserPeriodTimeService {
    ServiceResult findUserPeriodTime(String startDay, String endDay, String id);
}
