package com.sun.caishenye.octopus.common;

public class Utils {

    public static String formatNumber2String(String number) {
        return number.replace(Constants.DELIMITING_COMMA.getCode(), "");
    }
}
