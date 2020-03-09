package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;

/**
 * 晨星 自定义domain
 */
@Data
public class MorningStarExtendDomain extends MorningStarBaseDomain {

    // 基金ID
    private String fundId;

    // 年平均回报(%)
    private String returnAvg;
    // 风险
    private String risk;
    // 净值日期
    private String closePriceDate;
    // 单位净值
    private String closePrice;

    @Override
    public String toStr() {
        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_12.getCode()).append(fundId)
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
}
