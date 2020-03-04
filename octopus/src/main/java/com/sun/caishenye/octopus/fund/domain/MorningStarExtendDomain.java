package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constans;
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
        sbStr.append(Constans.DELIMITING_12.getCode()).append(fundId)
                .append(Constans.DELIMITING_12.getCode()).append(fundCode)
                .append(Constans.DELIMITING_12.getCode()).append(fundName)
                .append(Constans.DELIMITING_12.getCode()).append(return1Day)
                .append(Constans.DELIMITING_12.getCode()).append(return1Week)
                .append(Constans.DELIMITING_12.getCode()).append(return1Month)
                .append(Constans.DELIMITING_12.getCode()).append(return3Month)
                .append(Constans.DELIMITING_12.getCode()).append(return6Month)
                .append(Constans.DELIMITING_12.getCode()).append(return1Year)
                .append(Constans.DELIMITING_12.getCode()).append(return2Year)
                .append(Constans.DELIMITING_12.getCode()).append(return3Year)
                .append(Constans.DELIMITING_12.getCode()).append(return5Year)
                .append(Constans.DELIMITING_12.getCode()).append(return10Year)
                .append(Constans.DELIMITING_12.getCode()).append(returnInception);
        return sbStr.toString().replaceFirst(Constans.DELIMITING_12.getCode(), "");
    }
}
