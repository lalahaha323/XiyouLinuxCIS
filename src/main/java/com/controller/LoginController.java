package com.controller;

import com.config.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;
import com.util.AccessTokenUtil;
import com.util.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业内部E应用Quick-Start示例代码 实现了最简单的免密登录（免登）功能
 */
@RestController
public class LoginController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     * 使用指定类初始化日志对象，在日志输出的时候，可以打印出日志信息所在类
     * 之后在bizLogger.debug("日志信息");将会打印出LoginController:日志信息
     */
    private static final Logger bizLogger = LoggerFactory.getLogger(LoginController.class);


    /**
     * 欢迎页面,通过url访问，判断后端服务是否启动
     */
    @GetMapping(value = "/welcome")
    public String welcome() {
        return "welcome";
    }

    /**
     * 钉钉用户登录，显示当前登录用户的userId和名称
     *
     * @param requestAuthCode 免登临时code
     */
    @ResponseBody
    @PostMapping(value = "/login")
    public ServiceResult login(@RequestParam(value = "authCode") String requestAuthCode, HttpSession session) {
        //获取accessToken,注意正是代码要有异常流处理
        String accessToken = AccessTokenUtil.getToken();

        //获取用户信息
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_INFO);
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(requestAuthCode);
        request.setHttpMethod("GET");

        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
        //3.查询得到当前用户的userId
        // 获得到userId之后应用应该处理应用自身的登录会话管理（session）,避免后续的业务交互（前端到应用服务端）每次都要重新获取用户身份，提升用户体验
        String userId = response.getUserid();
        Map<String, Object> resultMap = getUserInformation(accessToken, userId);
        Map db_user = null;
        try {
            db_user = jdbcTemplate.queryForMap("SELECT * FROM user WHERE id = ?", userId);
            resultMap.put("mac", db_user.get("mac"));
        }catch (EmptyResultDataAccessException e){

        }
        session.setAttribute("user", resultMap);

        ServiceResult serviceResult = ServiceResult.success(resultMap);
        return serviceResult;
    }

    /**
     * 获取用户信息
     *
     * @param accessToken
     * @param userId
     * @return
     */
    private Map<String, Object> getUserInformation(String accessToken, String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_USER_GET);
            OapiUserGetRequest request = new OapiUserGetRequest();
            request.setUserid(userId);
            request.setHttpMethod("GET");
            OapiUserGetResponse response = client.execute(request, accessToken);

            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("userName", response.getName());
            map.put("userDepartment", response.getDepartment());
            map.put("userIsAdmin", response.getIsAdmin());
            return map;
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}


