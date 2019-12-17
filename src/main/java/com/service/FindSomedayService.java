package com.service;

import com.util.AllUser;

/**
 * @author lala
 */

public interface FindSomedayService {
    AllUser findRedis(String date, long time);
}
