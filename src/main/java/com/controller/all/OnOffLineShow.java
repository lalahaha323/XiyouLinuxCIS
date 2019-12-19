package com.controller.all;

import com.service.AllUserList;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代码说明：　返回所有用户的在线情况
 * 执行时间：　当想要看现在有什么人在线就会请求这个请求
 */

@CrossOrigin
@RestController
@RequestMapping("/all")
public class OnOffLineShow {

    @Autowired
    AllUserList allUserList;

    @GetMapping(value = "/onOffShow")
    public ServiceResult findSomeday() {
        return ServiceResult.success(allUserList.allUserList);
    }
}
