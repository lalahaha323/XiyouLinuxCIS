package com.filter;

import com.alibaba.fastjson.JSON;
import com.config.ResultCode;
import com.util.ServiceResult;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    JedisPool jedisPool;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");

        /** 已经登录就放行 **/
        if(httpServletRequest.getSession().getAttribute("web_user") != null) {
            return true;
        }

        /** 没有登录就继续登录 **/
        Jedis jedis = jedisPool.getResource();
        String passwd = httpServletRequest.getHeader("X-Token");
        String redis_passwd = jedis.get("wifi:Token");
        jedis.close();
        if(passwd != null && passwd.equals(redis_passwd))
            return true;

        PrintWriter out = httpServletResponse.getWriter();
        out.write(JSON.toJSONString(ServiceResult.failure(ResultCode.USER_NO_LOGIN_ERROR)));
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
