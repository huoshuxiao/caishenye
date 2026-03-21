package com.sun.caishenye.octopus.common;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.*;
import java.util.Random;

public class Utils {

    public static String long2Date(Long date) {
        return date == null ? "--" : LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()).toLocalDate().toString();
    }
    public static String long2DateTime(Long datetime) {
        return datetime == null ? "--" : LocalDateTime.ofInstant(Instant.ofEpochMilli(datetime), ZoneId.systemDefault()).toString();
    }

    public static Long date2Long(String date) {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(date), LocalTime.parse("00:00:00"));
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return  zdt.toInstant().toEpochMilli();
    }
    public static Long dateTime2Long(String datetime) {
        LocalDateTime localDateTime = LocalDateTime.parse(datetime);
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return  zdt.toInstant().toEpochMilli();
    }

    public static String getYear(String date) {
        return date.substring(0, 4);
    }

    public static String formatDate(String date) {

        if (date.length() < 10) {
            return "-";
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

    public static BigInteger random() {
        // 定义 21 位数的最小值 (10^20) 和最大值 (10^21 - 1)
        BigInteger min = new BigInteger("100000000000000000000"); // 1 后面 20 个 0
        BigInteger max = new BigInteger("999999999999999999999"); // 21 个 9

        // 使用 SecureRandom 保证随机性（适合 ID 生成），也可用 new Random()
        Random random = new SecureRandom();

        // 生成逻辑: randomValue = min + (random * (max - min + 1))
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        return min.add(new BigInteger(range.bitLength(), random).mod(range));
    }
}
