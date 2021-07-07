package com.sun.caishenye.octopus.common;

public class Utils {

    public static String formatDate(String date, String... fmt) {

        if (date.length() < 10) {
            return "-";
        }

        if (fmt != null) {
            date.substring(0, 10).replace("-", "");
        }

        return date.substring(0, 10);
    }

    // 去数值格式化
    public static String formatNumber2String(String number) {
        return number.trim().replaceAll(Constants.REGEX_DELIMITING_COMMA.getString(), "");
    }

    // 去横线
    public static String formatDate2String(String date) {
        return date.trim().replaceAll(Constants.REGEX_DELIMITING_HORIZONTAL_LINE.getString(), "");
    }

    // 百分比
    public static String rate(String numerator, String denominator) {
        if ("0".equals(denominator) || "0".equals(numerator) || "0.0".equals(denominator) || "0.0".equals(numerator)) {
            return "0";
        }
        double returnAvg = Double.valueOf(numerator.trim()) / Double.valueOf(denominator.trim()) * 100;
        return formatNumber2String(String.format("%.2f", returnAvg));
    }
}
