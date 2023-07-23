package com.macalester.mealplanner;

public class Utils {
    private Utils(){}

    public static String formatName(String name){
        return name.trim().toLowerCase().replaceAll(" +", " ");
    }
}
