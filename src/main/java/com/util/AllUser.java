package com.util;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lala
 * 作用：
 *  所有用户：
 *      1. 签到人数
 *      2. 未签到人数
 *      3. List<Map<String, Object>>一个签到人数数组
 *      4. List<Map<String, Object>>一个未签到人数数组
 */

@Data
public class AllUser {
    int CheckInPeople;
    int NoCheckInPeople;
    List<Map<String, Object>> CheckInUsers;
    List<Map<String, Object>> NoCheckInUsers;
}
