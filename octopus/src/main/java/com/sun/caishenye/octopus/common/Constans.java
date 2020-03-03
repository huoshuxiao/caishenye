package com.sun.caishenye.octopus.common;

import lombok.Getter;
import lombok.Setter;

public enum Constans {

    DELIMITING_COMMA(",");

    @Setter
    @Getter
    private String code;
    Constans(String code) {
        this.code = code;
    }
}
