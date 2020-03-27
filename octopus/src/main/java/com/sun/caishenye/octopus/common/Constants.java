package com.sun.caishenye.octopus.common;

import lombok.Getter;
import lombok.Setter;

public enum Constants {

    THREADS(10),
    FR_YUAN("元"),
    FR_10000(100000000),    // 亿
    EXCHANGE_SH("sh"),
    EXCHANGE_SZ("sz"),
    HQ_SUSPENSION("停牌"),
    SB_SCHEDULE_IMPLEMENT("实施"),
    SB_SCHEDULE_PLAN("预案"),
    SB_SCHEDULE_NOT_ASSIGNED("不分配"),
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
