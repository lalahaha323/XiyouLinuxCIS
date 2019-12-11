package com.service;

import com.util.ServiceResult;

import java.util.Set;

/**
 * @author lala
 */
public interface PushStatusService {
    void pushStatus(Set<String> onlineList);
}
