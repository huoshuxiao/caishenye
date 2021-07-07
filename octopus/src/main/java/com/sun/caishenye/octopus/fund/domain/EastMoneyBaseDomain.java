package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class EastMoneyBaseDomain {

    // 基金代码
    private String fundCode = "";
    // 基金名称
    private String fundName = "";
    // 成立日期
    private String inceptionDate = "";
    // 净值日期
    private String closePriceDate = "";
    // 单位净值
    private String closePrice = "";
    // 累计净值
    private String totalPrice = "";
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
    // 今年以来(%)
    protected String returnThisYear = "";
    // 设立以来(%)
    protected String returnInception = "";

    public String builder() {

        String sbStr = Constants.DELIMITING_COMMA.getString() + fundCode +
                Constants.DELIMITING_COMMA.getString() + fundName +
                Constants.DELIMITING_COMMA.getString() + inceptionDate +
                Constants.DELIMITING_COMMA.getString() + closePriceDate +
                Constants.DELIMITING_COMMA.getString() + closePrice +
                Constants.DELIMITING_COMMA.getString() + returnThisYear +
                Constants.DELIMITING_COMMA.getString() + return1Day +
                Constants.DELIMITING_COMMA.getString() + return1Week +
                Constants.DELIMITING_COMMA.getString() + return1Month +
                Constants.DELIMITING_COMMA.getString() + return3Month +
                Constants.DELIMITING_COMMA.getString() + return6Month +
                Constants.DELIMITING_COMMA.getString() + return1Year +
                Constants.DELIMITING_COMMA.getString() + return2Year +
                Constants.DELIMITING_COMMA.getString() + return3Year +
                Constants.DELIMITING_COMMA.getString() + return5Year +
                Constants.DELIMITING_COMMA.getString() + return10Year +
                Constants.DELIMITING_COMMA.getString() + returnInception +
                Constants.DELIMITING_COMMA.getString() + totalPrice;
        return sbStr.replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
