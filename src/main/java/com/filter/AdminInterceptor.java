package com.filter;

import com.alibaba.fastjson.JSON;
import com.config.ResultCode;
import com.util.ServiceResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author lala
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        Map<String, Object> user = (Map<String, Object>)httpServletRequest.getSession().getAttribute("web_user");
        /** 如果是管理员就放行 **/
        if((boolean)user.get("isAdmin")) {
            return true;
        }
        /** 如果是普通用户就阻止 **/
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.write(JSON.toJSONString(ServiceResult.failure(ResultCode.USER_NO_ADMIN_ERROR)));
        out.flush();
        out.close();
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
