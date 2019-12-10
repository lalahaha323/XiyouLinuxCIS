package com.util;

import lombok.Data;

import java.util.List;

/**
 * @author lala
 * 所有用户的某天的在线时间
 */

@Data
public class UserTime {
    String name;
    List<Time> timeList;
}
