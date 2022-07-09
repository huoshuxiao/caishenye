package com.sun.caishenye.rubikcube.dumy.fund.common;

import lombok.Getter;
import lombok.Setter;

public enum Constants {
    FUND_CODE("fund_code"),
    FUND_NAME("fund_name");

    @Setter
    @Getter
    private String code;
    Constants(String code) {
        this.code = code;
    }
}
