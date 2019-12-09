package com.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.service.AllUserList;
import com.util.ServiceResult;
import com.util.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lala
 */

@CrossOrigin
@RestController
public class OnOffLineShow {

    @Autowired
    AllUserList allUserList;

    @GetMapping(value = "/onOffShow")
    public ServiceResult findSomeday() {
        return ServiceResult.success(allUserList.allUserList);
    }
}
