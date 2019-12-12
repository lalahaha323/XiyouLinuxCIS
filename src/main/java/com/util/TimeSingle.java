package com.util;

import lombok.Data;

import java.util.List;

/**
 * @author lala
 */

@Data
public class TimeSingle {
    String date;
    String allTimeString;
    List<OnOffLine> onOffLines;
}
