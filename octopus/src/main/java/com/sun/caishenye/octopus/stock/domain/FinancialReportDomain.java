package com.sun.caishenye.octopus.stock.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 财务报表
 */
@Data
public class FinancialReportDomain implements Serializable {

    private static final long serialVersionUID = -7876077189407725206L;

    // 截止日期
    private String deadline;
    // 主营业务收入(亿元)
    private String mainBusinessIncome;
    // 净利润(亿元)
    private String netProfit;
    // 净利润率(净利润/主营业务收入)
    private String netMargin;

}
