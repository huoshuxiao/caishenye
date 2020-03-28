package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 股票 基础信息 bean
 *
 */
@Data
public class StockDomain implements Serializable {

    private static final long serialVersionUID = -1063860556029010836L;

    // 公司代码
    @JsonProperty("COMPANY_CODE")
    private String companyCode;

    // 公司简称
    @JsonProperty("COMPANY_ABBR")
    private String companyName;

    // 上市日期
    @JsonProperty("LISTING_DATE")
    private String listingDate;

    // 市盈率
    private String pe;

    // 股价
    private String price;
    // 交易日
    private String date;
    // 股息率
    private String dividendYield;

    // 证券交易所
    private String exchange;

    // 财务报表
    private FinancialReportDomain frDomain = new FinancialReportDomain();

    // 分红配股
    private ShareBonusDomain sbDomain = new ShareBonusDomain();

    public String baseBuilder() {

        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(exchange)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyCode)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName)
                .append(Constants.DELIMITING_COMMA.getString()).append(listingDate)
                ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }

    // 实时行情
    public String hqBuilder() {

        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName)
                .append(Constants.DELIMITING_COMMA.getString()).append(LocalDate.now())     // 交易日 2020-03-26
                .append(Constants.DELIMITING_COMMA.getString()).append(price)
        ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }

    // 财务报表
    public String frBuilder() {

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

    // 分红配股
    public String sbBuilder() {
        StringBuilder sbStr = new StringBuilder();

        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName)
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getBonusDate())         // 公告日期
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getDividend())          // 派息(税前)(元)
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getSchedule())          // 进度
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getDividendDate())      // 除权除息日
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getRegistrationDate())  // 股权登记日
        ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }

    // 钱多多
    public String mmBuilder() {

        StringBuilder sbStr = new StringBuilder();

        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName)
                .append(Constants.DELIMITING_COMMA.getString()).append(date)                           // 交易日
                .append(Constants.DELIMITING_COMMA.getString()).append(price)                           // 股价
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getBonusDate())         // 公告日期
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getDividend())          // 派息(税前)(元)
                .append(Constants.DELIMITING_COMMA.getString()).append(dividendYield)                   // 股息率
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getSchedule())          // 进度
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getDividendDate())      // 除权除息日
                .append(Constants.DELIMITING_COMMA.getString()).append(sbDomain.getRegistrationDate())  // 股权登记日
                ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
