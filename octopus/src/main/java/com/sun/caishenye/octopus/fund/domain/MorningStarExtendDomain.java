//package com.sun.caishenye.octopus.fund.domain;
//
//import com.sun.caishenye.octopus.common.Constants;
//import lombok.Data;
//
//import java.io.Serializable;
//
///**
// * 晨星 自定义domain
// */
//@Data
//public class MorningStarExtendDomain extends MorningStarBaseDomain implements Serializable {
//
//    private static final long serialVersionUID = -8012350405578695552L;
//
//    // 基金ID
//    private String fundId;
//
//    // 年平均回报(%)
//    private String returnAvg;
//    // 风险
//    private String risk;
//    // 净值日期
//    private String closePriceDate;
//    // 单位净值
//    private String closePrice;
//
//    // 基金类型
//    private String type;
//    // 基金规模
//    private String size;
//
//    @Override
//    public String toStr() {
//        StringBuilder sbStr = new StringBuilder();
//        sbStr.append(Constants.DELIMITING_12.getString()).append(fundId)
//                .append(Constants.DELIMITING_12.getString()).append(fundCode)
//                .append(Constants.DELIMITING_12.getString()).append(fundName)
//                .append(Constants.DELIMITING_12.getString()).append(return1Day)
//                .append(Constants.DELIMITING_12.getString()).append(return1Week)
//                .append(Constants.DELIMITING_12.getString()).append(return1Month)
//                .append(Constants.DELIMITING_12.getString()).append(return3Month)
//                .append(Constants.DELIMITING_12.getString()).append(return6Month)
//                .append(Constants.DELIMITING_12.getString()).append(return1Year)
//                .append(Constants.DELIMITING_12.getString()).append(return2Year)
//                .append(Constants.DELIMITING_12.getString()).append(return3Year)
//                .append(Constants.DELIMITING_12.getString()).append(return5Year)
//                .append(Constants.DELIMITING_12.getString()).append(return10Year)
//                .append(Constants.DELIMITING_12.getString()).append(returnInception);
//        return sbStr.toString().replaceFirst(Constants.DELIMITING_12.getString(), "");
//    }
//}
