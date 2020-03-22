package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;

/**
 * 股票 基础信息 bean
 *
 */
@Data
public class StockDomain {

    // 公司代码
    @JsonProperty("COMPANY_CODE")
    private String companyCode;

    // 公司简称
    @JsonProperty("COMPANY_ABBR")
    private String companyName;

    // 上市日期
    @JsonProperty("LISTING_DATE")
    private String listingDate;

    // 证券交易所
    private String exchange;

    // 财务报表
    private FinancialReportDomain frDomain = new FinancialReportDomain();

    public String toStr() {

        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(exchange)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyCode)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName)
                .append(Constants.DELIMITING_COMMA.getString()).append(listingDate)
                ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }

    // 财务报表
    public Object toFrStr() {

        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName)
                .append(Constants.DELIMITING_COMMA.getString()).append(frDomain.getDeadline())
                .append(Constants.DELIMITING_COMMA.getString()).append(frDomain.getMainBusinessIncome())
                .append(Constants.DELIMITING_COMMA.getString()).append(frDomain.getNetProfit())
                .append(Constants.DELIMITING_COMMA.getString()).append(frDomain.getNetMargin())
        ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
