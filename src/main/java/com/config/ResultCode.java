package com.config;

/**
 * @author lala
 * 所有返回信息的状态码
 */

public enum ResultCode {

    /** 关于用户相关的都以700开头 **/
    USER_NOT_EXIST_GROUP_ERROR("7001", "用户不属于小组成员"),
    USER_NO_ERROR("7002", "数据库中没有这个用户"),

    /** 和日期相关的都以720开头 **/
    DATE_NO_ENTER_ERROR("7201", "没有输入日期"),
    DATE_FORMATTER_ERROR("7202", "输入日期格式不正确"),
    DATE_LESSTHAN_ERROR("7203", "只能输入小于今天的日期"),
    DATE_LESSTHAN_MONTH_ERROR("7204", "只能输入小于今天的月份"),

    /** 时间相关的都以800开头 **/
    TIME_OBSOLETE_ERROR("8001", "时间超时，请重新登录"),
    TIME_LESSTHAN_ERROE("8002"),

    /** sql执行相关都以900开头 **/
    SQL_EXECUTE_ERROE("9001", "系统执行错误")



    ;

    /** 错误码和错误描述 **/
    private String code;
    private String messgae;

    ResultCode(String code) {
        this.code = code;
    }

    ResultCode(String code, String messgae) {
        this.code = code;
        this.messgae = messgae;
    }

    /** getter和setter方法 **/
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

}
