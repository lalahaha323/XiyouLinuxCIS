package com.controller.other;

import com.service.PushStatusService;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Set;

@RestController
@RequestMapping("/other")
public class PushStatusController {

    @Autowired
    PushStatusService pushStatusService;

    @PostMapping("/push_status")
    public ServiceResult pushStatus(@RequestBody Set<String> onlineList){
        return pushStatusService.pushStatus(onlineList);
    }
}
