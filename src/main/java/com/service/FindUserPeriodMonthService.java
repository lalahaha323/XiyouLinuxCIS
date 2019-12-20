package com.service;

import com.util.ServiceResult;

/**
 * @author lala
 */
public interface FindUserPeriodMonthService {
    ServiceResult findUSerPeriodMonth(String startMonth, String endMonth, String id);
}
