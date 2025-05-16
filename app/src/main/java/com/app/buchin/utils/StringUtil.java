package com.app.buchin.utils;

public class StringUtil {

    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty() || ("").equals(input.trim());
    }

    public static String getDoubleNumber(int number) {
        if (number < 10) {
            return "0" + number;
        } else return "" + number;
    }
}
