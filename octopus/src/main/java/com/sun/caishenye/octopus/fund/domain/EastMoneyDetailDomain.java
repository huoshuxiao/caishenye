package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
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
    // 基金类型
    private String type;
    // 基金规模
    private String size;

    public String toStr() {

        StringBuilder sbStr = new StringBuilder();

        sbStr.append(Constants.DELIMITING_12.getString()).append(fundCode)
                .append(Constants.DELIMITING_12.getString()).append(fundName)
                .append(Constants.DELIMITING_12.getString()).append(type)
                .append(Constants.DELIMITING_12.getString()).append(size)
                .append(Constants.DELIMITING_12.getString()).append(closePriceDate)
                .append(Constants.DELIMITING_12.getString()).append(closePrice)
                .append(Constants.DELIMITING_12.getString()).append(risk)
        ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_12.getString(), "");
    }
}
