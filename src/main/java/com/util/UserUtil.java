package com.util;

import lombok.Data;

import java.util.List;

/**
 * @author lala
 */

/**
 * 一个用户的所有属性
 */
@Data
public class UserUtil implements Comparable<UserUtil>{
    String id;
    String name;
    int department;
    String allTimeString;
    int allTimeInt;
    List<OnOffLineUtil> onOffLineUtils;

    @Override
    public int compareTo(UserUtil userUtil) {
        int time = ((UserUtil)userUtil).getAllTimeInt();
        return time - this.allTimeInt;
    }
}
