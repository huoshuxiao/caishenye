package com.sun.caishenye.octopus.common;

import org.apache.commons.lang3.StringUtils;

public class Utils {

    public static String formatNumber2String(String number) {
        return number.trim().replace(Constants.DELIMITING_COMMA.getString(), "");
    }

    public static String rate(String numerator, String denominator) {
        double returnAvg = Double.valueOf(StringUtils.isEmpty(numerator.trim()) ? "0" : numerator.trim()) / Double.valueOf(StringUtils.isEmpty(denominator.trim()) ? "0" : denominator.trim()) * 100;
        return formatNumber2String(String.format("%.2f", returnAvg));
    }
}
