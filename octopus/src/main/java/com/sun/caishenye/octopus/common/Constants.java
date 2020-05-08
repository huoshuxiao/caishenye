package com.sun.caishenye.octopus.common;

import lombok.Getter;
import lombok.Setter;

public enum Constants {

    FILE_MORNING_STAR_BASE("MorningStarBase.log"),
    FILE_MORNING_STAR_EXTEND("MorningStarExtend.log"),
    FILE_EAST_MONEY_DETAIL("EastMoneyDetail.log"),
    FILE_FUND("Fund.csv"),

    FILE_FINANCIAL_REPORT_STEP1("FinancialReportStep1.log"),
    FILE_FINANCIAL_REPORT_STEP2("FinancialReportStep2.log"),
    FILE_FINANCIAL_REPORT_EASTMONEY("FinancialReportEastMoney.log"),
    FILE_FINANCIAL_REPORT("FinancialReport_bak.csv"),
    FILE_FINANCIAL_REPORT2("Stock_FinancialReport.csv"),
    FILE_SHARE_BONUS("ShareBonus.log"),
    FILE_HQ("HQ.log"),
    FILE_HHQ("HHQ.log"),
    FILE_MONEY_MONEY("Stock_MM.csv"),

    CHROME_DRIVER_PATH("/home/sunwenkun/Developer/gitroot/caishenye.git/octopus/bin/chromedriver"),

    THREADS(10),

    FR_YUAN("元"),
    FR_10000(100000000),    // 亿
    EXCHANGE_SH("sh"),
    EXCHANGE_SZ("sz"),
    HQ_SUSPENSION("停牌"),
    SB_SCHEDULE_IMPLEMENT("实施"),
    SB_SCHEDULE_PLAN("预案"),
    SB_SCHEDULE_NOT_ASSIGNED("不分配"),

    REGEX_DELIMITING_HORIZONTAL_LINE("[-]"),
    REGEX_DELIMITING_COMMA(","),

    DELIMITING_COMMA(","),
    DELIMITING_12("!@");

    @Setter
    @Getter
    private String string;

    @Setter
    @Getter
    private Integer integer;


    Constants(String string) {
        this.string = string;
    }

    Constants(Integer integer) {
        this.integer = integer;
    }
}
