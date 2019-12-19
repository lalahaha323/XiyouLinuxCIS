package com.controller.other;

import com.config.Constant;
import com.config.ResultCode;
import com.config.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetUseridByUnionidRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetUseridByUnionidResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;
import com.util.AccessTokenUtil;
import com.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.config.URLConstant.URL_GET_BYCODE;
import static com.config.URLConstant.URL_GET_USER_USERID;

/**
 * 企业内部E应用Quick-Start示例代码 实现了最简单的免密登录（免登）功能
 * 代码说明： 显示用户姓名
 * 发生时间： 当用户点击小程序的时候，就会发送请求到这里，然后后台做一些操作把用户名返回给前端
 */
@RestController
@RequestMapping("/other")
public class LoginController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 欢迎页面,通过url访问，判断后端服务是否启动 **/
    @GetMapping(value = "/welcome")
    public String welcome() {
        return "welcome";
    }

    /** 钉钉用户登录，显示当前登录用户的userId和名称 **/
    @PostMapping(value = "/login")
    public ServiceResult login(@RequestParam(value = "authCode") String requestAuthCode, HttpSession session) {

        /** 获取accessToken,注意正式代码要有异常流处理 **/
        String accessToken = AccessTokenUtil.getToken();

        /** 获取用户信息 **/
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

        /** 查询得到当前用户的userId **/
        /** 获得到userId之后应用应该处理应用自身的登录会话管理（session）,避免后续的业务交互（前端到应用服务端）每次都要重新获取用户身份，提升用户体验 **/
        String userId = response.getUserid();
        Map<String, Object> resultMap = getUserInformation(accessToken, userId);
        Map db_user = null;
        try {
            db_user = jdbcTemplate.queryForMap("SELECT * FROM user WHERE id = ?", userId);
            resultMap.put("mac", db_user.get("mac"));
        }catch (EmptyResultDataAccessException e){
            /** 数据中没有这个用户 **/
            e.printStackTrace();
            return ServiceResult.failure(ResultCode.USER_NO_ERROR);
        }
        session.setAttribute("user", resultMap);
        return ServiceResult.success(resultMap);
    }


    /** 传过来一个临时码，通过临时码获取用户的unionID，然后通过unionId获取用户的userID，然后从数据库中通过userID将用户的必要信息传入session中 **/
    @CrossOrigin
    @GetMapping(value = "/getWebUser")
    public ServiceResult getInfoByTmpCode(@RequestParam("code") String tmpCode, HttpSession session) throws ApiException {

        /** 通过临时码获取unionID **/
        DefaultDingTalkClient  client = new DefaultDingTalkClient(URL_GET_BYCODE);
        OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
        req.setTmpAuthCode(tmpCode);
        OapiSnsGetuserinfoBycodeResponse response = client.execute(req, Constant.CODE_APP_KEY,Constant.CODE_APP_SECRET);
        if(response.isSuccess()){
            String unionID = response.getUserInfo().getUnionid();

            /** 获取accessToken,注意正式代码要有异常流处理 **/
            String accessToken = AccessTokenUtil.getToken();

            /** 通过unionID获取userID **/
            client = new DefaultDingTalkClient(URL_GET_USER_USERID);
            OapiUserGetUseridByUnionidRequest request = new OapiUserGetUseridByUnionidRequest();
            request.setUnionid(unionID);
            request.setHttpMethod("GET");
            OapiUserGetUseridByUnionidResponse getUseridByUnionidResponse = null;
            try{
                getUseridByUnionidResponse = client.execute(request, accessToken);
            } catch (ApiException e) {
                e.printStackTrace();
            }
            String userID = getUseridByUnionidResponse.getUserid();

            /** 通过userID获取用户重要信息 **/
            try {
                Map<String, Object> user = jdbcTemplate.queryForMap("SELECT name, isAdmin FROM user WHERE id = ?", userID);
                user.put("id", userID);
                session.setAttribute("web_user", user);
                return ServiceResult.success(user);
            }catch (EmptyResultDataAccessException e){
                return ServiceResult.failure(ResultCode.USER_NOT_EXIST_GROUP_ERROR);
            }
        }else {
            /** 获取unionId失败 **/
            return ServiceResult.failure(response.getErrorCode(), response.getErrmsg());
        }
    }

    @CrossOrigin
    @GetMapping("/getInfo")
    public ServiceResult getInfo(HttpSession session){
        Map user = (Map)session.getAttribute("web_user");
        if(user == null){
            return ServiceResult.failure(ResultCode.USER_NO_LOGIN);
        }
        return ServiceResult.success(user);
    }
    /** 获取用户信息 **/
    private Map<String, Object> getUserInformation(String accessToken, String userId) {

        DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_USER_GET);
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("GET");
        OapiUserGetResponse response = null;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("userName", response.getName());
        map.put("userDepartment", response.getDepartment());
        map.put("userIsAdmin", response.getIsAdmin());
        return map;
    }
}


