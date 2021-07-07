package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;

import java.io.Serializable;

@Data
public class EastMoneyDetailDomain implements Serializable {

    private static final long serialVersionUID = -7580702962885572641L;

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
    // 基金经理
    private String managerName;
    // 管理期间 2016-06-24~至今
    private String managementRange;
    // 管理时间 4年又315天
    private String managementTime;
    // 管理回报(%) 80.58%
    private String managementReturn;

    public String builder() {

        String sbStr = Constants.DELIMITING_COMMA.getString() + fundCode +
                Constants.DELIMITING_COMMA.getString() + fundName +
                Constants.DELIMITING_COMMA.getString() + type +
                Constants.DELIMITING_COMMA.getString() + size +
                Constants.DELIMITING_COMMA.getString() + managerName +
                Constants.DELIMITING_COMMA.getString() + managementRange +
                Constants.DELIMITING_COMMA.getString() + managementTime +
                Constants.DELIMITING_COMMA.getString() + managementReturn +
                Constants.DELIMITING_COMMA.getString() + closePriceDate +
                Constants.DELIMITING_COMMA.getString() + closePrice +
                Constants.DELIMITING_COMMA.getString() + risk;
        return sbStr.replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
