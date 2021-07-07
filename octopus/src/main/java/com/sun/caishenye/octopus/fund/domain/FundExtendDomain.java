package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import lombok.Data;

import java.io.Serializable;

/**
 * 基金 自定义指标domain
 */
@Data
public class FundExtendDomain implements Serializable {

    private static final long serialVersionUID = 5296550176209015089L;

    // 基金代码
    private String fundCode;
    // 管理期间 年平均回报(%)
    private String returnAvg;

    public String builder() {
        String sbStr = Constants.DELIMITING_COMMA.getString() + fundCode +
                Constants.DELIMITING_COMMA.getString() + returnAvg;
        return sbStr.replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
