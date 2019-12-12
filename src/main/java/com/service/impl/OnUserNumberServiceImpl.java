package com.service.impl;

import com.service.AllUserList;
import com.service.OnUserNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lala
 */

@Service
@Slf4j
public class OnUserNumberServiceImpl implements OnUserNumberService {
    @Autowired
    AllUserList allUserList;

    @Override
    public int onUserNumber() {
        return allUserList.onLineNumber;
    }
}
