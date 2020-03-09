package com.sun.caishenye.octopus.common;

import lombok.Getter;
import lombok.Setter;

public enum Constants {

    DELIMITING_COMMA(","), DELIMITING_12("!@");

    @Setter
    @Getter
    private String code;

    Constants(String code) {
        this.code = code;
    }
}
