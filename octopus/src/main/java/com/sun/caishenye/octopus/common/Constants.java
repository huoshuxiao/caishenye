package com.sun.caishenye.octopus.common;

import lombok.Getter;
import lombok.Setter;

public enum Constants {

    THREADS(10),
    DELIMITING_COMMA(","),
    DELIMITING_12("!@");

    @Setter
    @Getter
    private String code;

    @Setter
    @Getter
    private Integer count;


    Constants(String code) {
        this.code = code;
    }

    Constants(Integer count) {
        this.count = count;
    }
}
