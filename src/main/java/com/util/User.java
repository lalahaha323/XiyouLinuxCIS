package com.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @author lala
 * 一个用户的所有属性
 */

@Data
public class User{
    String id;
    String name;
    int department;
    String allTimeString;
    int allTimeInt;
    boolean isOnline;
    @JsonIgnore
    String mac;
    List<OnOffLine> onOffLine;
}
