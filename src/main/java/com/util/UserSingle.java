package com.util;

import lombok.Data;

import java.util.List;

/**
 * @author lala
 */

@Data
public class UserSingle {
    String name;
    int department;
    List<TimeSingle> timeSingles;
}
