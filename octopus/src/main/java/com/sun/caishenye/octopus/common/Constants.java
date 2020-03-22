package com.sun.caishenye.octopus.common;

import lombok.Getter;
import lombok.Setter;

public enum Constants {

    FR_YUAN("元"),
    EXCHANGE_SH("sh"),
    EXCHANGE_SZ("sz"),
    THREADS(10),
    FR_10000(100000000),    // 亿
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
