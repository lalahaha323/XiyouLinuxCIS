package com.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


/**
 * @author lala
 * 一个用户的所有属性
 */

@Data
public class User{
    String id;
    String name;
    int department;
    @JsonIgnore
    String mac;
    boolean isOnline;
}
