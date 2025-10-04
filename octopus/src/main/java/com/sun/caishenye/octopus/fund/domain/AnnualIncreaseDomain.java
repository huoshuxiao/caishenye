package com.sun.caishenye.octopus.fund.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 年度涨跌幅
 */
public class AnnualIncreaseDomain {

    // 基金代码
    @Setter
    @Getter
    private String fundCode;

    // 基金名称
    @Setter
    @Getter
    private String fundName;

    // 年度
    @Setter
    @Getter
    private String year;

    // 涨跌额(%)
    @Setter
    @Getter
    private String increase;

    public String[] builders() {
        return new String[]{fundCode, fundName, year, increase};
    }
}
