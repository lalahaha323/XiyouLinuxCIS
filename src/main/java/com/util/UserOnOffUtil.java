package com.util;

import lombok.Data;

import java.util.List;

/**
 * @author lala
 */

/**
 * 返回的List中的对象，封装了
 *      一个类用来存一个用户A的所有属性
 *      一个List用来存所有上下线的时间段
 */
@Data
public class UserOnOffUtil {
    UserUtil userUtil;
    List<OnOffLineUtil> onOffLineUtils;
}
