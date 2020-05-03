package com.sun.caishenye.octopus.stock.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 财务报表
 */
@Data
public class FinancialReport2Domain extends StockDomain implements Serializable {

    private static final long serialVersionUID = 2468960293429726322L;

    // 截止日期
    @JsonProperty("reportdate")
    private String deadline;
    // 主营业务收入(亿元)
    private String mainBusinessIncome;
    // 净利润(亿元)
    private String netProfit;
    // 净利润率(净利润/主营业务收入)
    private String netMargin;

    /* 成长能力 */
    // 主营业务收入增长率(%)(同比)YoY
    private String mainBusinessIncomeGrowthRate;
    // 主营业务收入增长率(%)(环比)MoM
    private String mainBusinessIncomeGrowthRateMoM;
    // 净利润增长率(%)(同比)YoY
    private String netProfitGrowthRate;
    // 净利润增长率(%)(环比)MoM
    private String netProfitGrowthRateMoM;

    // 每股收益(元)
    private String basicEps;
    // 每股收益(扣除)(元)
    private String cutBasicEps;
    // 每股净资产(元)
    private String bps;
    // 净资产收益率(%)
    private String roeWeighted;
    // 每股经营现金流量(元)
    private String perShareCashFlowFromOperations;
    // 销售毛利率(%)
    private String grossProfitMargin;
    // 利润分配
    private String profitDistribution;
    // 股息率(%)
    private String dividendYield;
    // 首次公告日期
    private String firstNoticeDate;
    // 最新公告日期
    private String latestNoticeDate;

    public String builder() {

        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(this.getCompanyCode())
                .append(Constants.DELIMITING_COMMA.getString()).append(this.getCompanyName())
                .append(Constants.DELIMITING_COMMA.getString()).append(this.getTradeMarket())   // 上交所主板
                .append(Constants.DELIMITING_COMMA.getString()).append(this.getPublishName())   // 所属行业
                .append(Constants.DELIMITING_COMMA.getString()).append(LocalDate.now())         // 交易日 2020-03-26
//                .append(Constants.DELIMITING_COMMA.getString()).append(this.getPrice())       // 股价
                .append(Constants.DELIMITING_COMMA.getString()).append(deadline)                // 截止日期
                .append(Constants.DELIMITING_COMMA.getString()).append(mainBusinessIncome)      // 主营业务收入(亿元)
                .append(Constants.DELIMITING_COMMA.getString()).append(netProfit)               // 净利润(亿元)
                .append(Constants.DELIMITING_COMMA.getString()).append(netMargin)               // 净利润率(净利润/主营业务收入)
                .append(Constants.DELIMITING_COMMA.getString()).append(mainBusinessIncomeGrowthRate)      // 主营业务收入增长率(%)(同比)
                .append(Constants.DELIMITING_COMMA.getString()).append(netProfitGrowthRate)               // 净利润增长率(%)(同比)
                .append(Constants.DELIMITING_COMMA.getString()).append(mainBusinessIncomeGrowthRateMoM)   // 主营业务收入增长率(%)(环比)
                .append(Constants.DELIMITING_COMMA.getString()).append(netProfitGrowthRateMoM)            // 净利润增长率(%)(环比)
                .append(Constants.DELIMITING_COMMA.getString()).append(basicEps)           // 每股收益(元)
                .append(Constants.DELIMITING_COMMA.getString()).append(cutBasicEps)        // 每股收益(扣除)(元)
                .append(Constants.DELIMITING_COMMA.getString()).append(bps)                // 每股净资产(元)
                .append(Constants.DELIMITING_COMMA.getString()).append(roeWeighted)        // 净资产收益率(%)
                .append(Constants.DELIMITING_COMMA.getString()).append(perShareCashFlowFromOperations)  // 每股经营现金流量(元)
                .append(Constants.DELIMITING_COMMA.getString()).append(grossProfitMargin)               // 销售毛利率(%)
//                .append(Constants.DELIMITING_COMMA.getString()).append(profitDistribution)            // 利润分配
//                .append(Constants.DELIMITING_COMMA.getString()).append(dividendYield)                 // 股息率(%)
                .append(Constants.DELIMITING_COMMA.getString()).append(firstNoticeDate)                 // 首次公告日期
                .append(Constants.DELIMITING_COMMA.getString()).append(latestNoticeDate)                // 最新公告日期
        ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
