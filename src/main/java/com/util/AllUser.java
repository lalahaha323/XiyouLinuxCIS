package com.util;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lala
 * 所有用户，包括签到人数，未签到人数，List<UserUtil>一个签到人数数组
 * eg:
 * AllUser
 *  CheckInPeople = x;
 *  NoCheckInPeople = y;
 *  userUtils:
 *      [String id; String name; int department; String allTimeString; int allTimeInt; List<OnOffLineUtil> onOffLineUtils;],
 *      [].
 *      [],
 *      ......
 */

@Data
public class AllUser {
    int CheckInPeople;
    int NoCheckInPeople;
    List<Map<String, Object>> users;
}
