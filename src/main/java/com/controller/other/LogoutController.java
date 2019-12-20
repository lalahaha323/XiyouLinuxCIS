package com.controller.other;

import com.util.ServiceResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author lala
 */

@RestController
@RequestMapping("/other")
public class LogoutController {

    @GetMapping(value = "/logout")
    public ServiceResult logout(HttpSession httpSession) {
        httpSession.removeAttribute("web_user");
        return ServiceResult.success("注销成功                                                                                                                                                                    ");
    }
}
