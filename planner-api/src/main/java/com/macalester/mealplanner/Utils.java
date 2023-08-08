package com.macalester.mealplanner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private Utils(){}

    public static String formatName(String name){
        return name.trim().toLowerCase().replaceAll(" +", " ");
    }

    public static String computeFilename(String filePrefix) {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        return filePrefix + "_" + date.format(new Date()) + ".csv";
    }
}
