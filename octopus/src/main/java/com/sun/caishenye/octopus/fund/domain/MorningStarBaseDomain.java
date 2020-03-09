package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;

/**
 * http://cn.morningstar.com/quickrank/default.aspx
 * 业绩和风险 Bean
 */
@Data
public class MorningStarBaseDomain {

    protected String page = "";
    // 基金代码
    protected String fundCode = "";
    // 基金名称
    protected String fundName = "";
    // 1天回报(%)
    protected String return1Day = "";
    // 1周回报(%)
    protected String return1Week = "";
    // 1个月回报(%)
    protected String return1Month = "";
    // 3个月回报(%)
    protected String return3Month = "";
    // 6个月回报(%)
    protected String return6Month = "";
    // 1年回报(%)
    protected String return1Year = "";
    // 2年回报(%)
    protected String return2Year = "";
    // 3年回报(%)
    protected String return3Year = "";
    // 5年回报(%)
    protected String return5Year = "";
    // 10年回报(%)
    protected String return10Year = "";
    // 设立以来(%)
    protected String returnInception = "";

    public String toStr() {

//        JSONObject jsonObject = toJsonObject();
        StringBuilder sbStr = new StringBuilder();
        // 乱序
//        jsonObject.values().forEach(v -> {
//            sbStr.append(",").append(v);
//        });

        sbStr//.append(Constans.DELIMITING_6.getCode()).append(page)
                .append(Constants.DELIMITING_12.getCode()).append(fundCode)
                .append(Constants.DELIMITING_12.getCode()).append(fundName)
                .append(Constants.DELIMITING_12.getCode()).append(return1Day)
                .append(Constants.DELIMITING_12.getCode()).append(return1Week)
                .append(Constants.DELIMITING_12.getCode()).append(return1Month)
                .append(Constants.DELIMITING_12.getCode()).append(return3Month)
                .append(Constants.DELIMITING_12.getCode()).append(return6Month)
                .append(Constants.DELIMITING_12.getCode()).append(return1Year)
                .append(Constants.DELIMITING_12.getCode()).append(return2Year)
                .append(Constants.DELIMITING_12.getCode()).append(return3Year)
                .append(Constants.DELIMITING_12.getCode()).append(return5Year)
                .append(Constants.DELIMITING_12.getCode()).append(return10Year)
                .append(Constants.DELIMITING_12.getCode()).append(returnInception);

        return sbStr.toString().replaceFirst(Constants.DELIMITING_12.getCode(), "");
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
