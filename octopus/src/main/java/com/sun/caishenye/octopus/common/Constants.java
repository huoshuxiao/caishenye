package com.sun.caishenye.octopus.common;

import lombok.Getter;
import lombok.Setter;

public enum Constants {

    FILE_MORNING_STAR_BASE("MorningStarBase.log"),
    FILE_MORNING_STAR_EXTEND("MorningStarExtend.log"),
    FILE_EAST_MONEY_DETAIL("EastMoneyDetail.log"),
    FILE_FUND("Fund.csv"),

    FILE_FINANCIAL_REPORT_STEP1("FinancialReportStep1.csv"),
    FILE_FINANCIAL_REPORT_STEP2("FinancialReportStep2.csv"),
    FILE_FINANCIAL_REPORT("FinancialReport.csv"),
    FILE_SHARE_BONUS("ShareBonus.csv"),
    FILE_HQ("HQ.csv"),
    FILE_HHQ("HHQ.csv"),
    FILE_MONEY_MONEY("MoneyMoney.csv"),

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
