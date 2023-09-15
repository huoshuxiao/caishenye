package com.sun.caishenye.octopus.common;

import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    void testDate() {
        long d = 1694707200000L;
        System.out.println(Utils.long2Date(d));
        System.out.println(Utils.date2Long(Utils.long2Date(d)));
    }
}
