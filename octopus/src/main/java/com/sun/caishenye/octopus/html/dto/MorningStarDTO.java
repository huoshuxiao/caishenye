package com.sun.caishenye.octopus.html.dto;

import lombok.Data;

@Data
public class MorningStarDTO {

    private String page = "";
    // 基金代码
    private String fundCode = "";
    // 基金名称
    private String fundName = "";
    // 1天回报(%)
    private String return1Day = "";
    // 1周回报(%)
    private String return1Week = "";
    // 1个月回报(%)
    private String return1Month = "";
    // 3个月回报(%)
    private String return3Month = "";
    // 6个月回报(%)
    private String return6Month = "";
    // 1年回报(%)
    private String return1Year = "";
    // 2年回报(%)
    private String return2Year = "";
    // 3年回报(%)
    private String return3Year = "";
    // 5年回报(%)
    private String return5Year = "";
    // 10年回报(%)
    private String return10Year = "";
    // 设立以来
    private String returnInception = "";

    public String toStr() {

//        JSONObject jsonObject = toJsonObject();
        StringBuilder sbStr = new StringBuilder();
        // 乱序
//        jsonObject.values().forEach(v -> {
//            sbStr.append(",").append(v);
//        });

        sbStr//.append(",").append(page)
                .append(",").append(fundCode)
                .append(",").append(fundName)
                .append(",").append(return1Day)
                .append(",").append(return1Week)
                .append(",").append(return1Month)
                .append(",").append(return3Month)
                .append(",").append(return6Month)
                .append(",").append(return1Year)
                .append(",").append(return2Year)
                .append(",").append(return3Year)
                .append(",").append(return5Year)
                .append(",").append(return10Year)
                .append(",").append(returnInception);

        return sbStr.toString().replaceFirst(",", "");
    }

//    public JSONObject toJsonObject() {
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("fundCode", fundCode);
//        jsonObject.put("fundName", fundName);
//        jsonObject.put("return1Day", return1Day);
//        jsonObject.put("return1Week", return1Week);
//        jsonObject.put("return1Month", return1Month);
//        jsonObject.put("return3Month", return3Month);
//        jsonObject.put("return6Month", return6Month);
//        jsonObject.put("return1Year", return1Year);
//        jsonObject.put("return2Year", return2Year);
//        jsonObject.put("return3Year", return3Year);
//        jsonObject.put("return5Year", return5Year);
//        jsonObject.put("return10Year", return10Year);
//        jsonObject.put("returnInception", returnInception);
//        return jsonObject;
//    }
}
