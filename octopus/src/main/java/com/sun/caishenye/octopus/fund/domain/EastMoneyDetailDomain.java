package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constans;
import lombok.Data;

@Data
public class EastMoneyDetailDomain {

    // 风险
    private Boolean risk = Boolean.FALSE;
    // 基金代码
    protected String fundCode = "";
    // 基金名称
    protected String fundName = "";
    // 净值日期
    private String closePriceDate;
    // 单位净值
    private String closePrice;

    public String toStr() {

        StringBuilder sbStr = new StringBuilder();

        sbStr.append(Constans.DELIMITING_12.getCode()).append(fundName)
                .append(Constans.DELIMITING_12.getCode()).append(fundCode)
//                .append(Constans.DELIMITING_6.getCode()).append(fundName)
                .append(Constans.DELIMITING_12.getCode()).append(closePriceDate)
                .append(Constans.DELIMITING_12.getCode()).append(closePrice)
                .append(Constans.DELIMITING_12.getCode()).append(risk)
        ;
        return sbStr.toString().replaceFirst(Constans.DELIMITING_12.getCode(), "");
    }
}
