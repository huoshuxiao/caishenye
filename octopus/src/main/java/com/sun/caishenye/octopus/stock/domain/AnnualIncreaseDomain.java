package com.sun.caishenye.octopus.stock.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 年度涨跌幅
 */
public class AnnualIncreaseDomain {

    // 公司代码
    @Setter
    @Getter
    private String companyCode;

    // 公司简称
    @Setter
    @Getter
    private String companyName;

    // 年度
    @Setter
    @Getter
    private String year;

    // 涨跌额(%)
    @Setter
    @Getter
    private String increase;

    public String[] builders() {
        return new String[]{companyCode, companyName, year, increase};
    }
}
