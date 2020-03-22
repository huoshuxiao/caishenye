package com.sun.caishenye.octopus.stock.domain;

import lombok.Data;

/**
 * 财务报表
 */
@Data
public class FinancialReportDomain {

    // 截止日期
    private String deadline;
    // 主营业务收入(万元)
    private String mainBusinessIncome;
    // 净利润(万元)
    private String netProfit;
    // 净利润率(净利润/主营业务收入)
    private String netMargin;

}
