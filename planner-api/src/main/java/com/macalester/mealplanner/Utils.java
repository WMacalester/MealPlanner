package com.macalester.mealplanner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private Utils(){}

    public static String formatName(String name){
        return name.trim().toLowerCase().replaceAll(" +", " ");
    }

    public static String formatCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(new Date());
    }
}
