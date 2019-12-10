package com.service.impl;

import com.service.AllUserList;
import com.service.OnUserNumberService;
import com.util.ServiceResult;
import com.util.User;
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
    public ServiceResult onUserNumber() {
        int i = 0;
        for(User user : allUserList.allUserList) {
            if(user.isOnline())
                i++;
        }
        return ServiceResult.success(i);
    }
}
