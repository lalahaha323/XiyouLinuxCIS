package com.config;

public class URLConstant {

    /** 钉钉网关gettoken地址 **/
    public static final String URL_GET_TOKKEN = "https://oapi.dingtalk.com/gettoken";

    /** 获取用户在企业内userId的接口URL **/
    public static final String URL_GET_USER_INFO = "https://oapi.dingtalk.com/user/getuserinfo";

    /** 获取用户信息的接口url **/
    public static final String URL_USER_GET = "https://oapi.dingtalk.com/user/get";

    /** 获取用户扫二维码登录时的临时码 **/
    public static final String URL_GET_BYCODE = "https://oapi.dingtalk.com/sns/getuserinfo_bycode";

    /** 根据unionid获取userid **/
    public static final String URL_GET_USER_USERID = "https://oapi.dingtalk.com/user/getUseridByUnionid";
}
