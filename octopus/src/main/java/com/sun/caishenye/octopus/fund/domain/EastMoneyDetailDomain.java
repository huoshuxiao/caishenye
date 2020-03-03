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

    public String toStr() {

        StringBuilder sbStr = new StringBuilder();

        sbStr.append(Constans.DELIMITING_COMMA.getCode()).append(fundName)
                .append(Constans.DELIMITING_COMMA.getCode()).append(fundCode)
//                .append(Constans.DELIMITING_COMMA.getCode()).append(fundName)
                .append(Constans.DELIMITING_COMMA.getCode()).append(risk)
        ;
        return sbStr.toString().replaceFirst(Constans.DELIMITING_COMMA.getCode(), "");
    }
}
