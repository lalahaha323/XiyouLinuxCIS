package com.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.List;

/**
 * @author lala
 * 一个用户的所有属性
 */

@Data
public class User{
    String id;
    @JsonView(View.class)
    String name;
    @JsonView(View.class)
    int department;
    String allTimeString;
    @JsonView(View.class)
    int allTimeInt;
    @JsonView(View.class)
    boolean isOnline;
    @JsonIgnore
    String mac;
    List<OnOffLine> onOffLine;
}
