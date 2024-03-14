package com.sun.caishenye.octopus.common;

import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    void testDate() {
        long d = 1710247928283L;
        System.out.println(Utils.long2Date(d));
        System.out.println(Utils.long2DateTime(d));
        System.out.println(Utils.date2Long(Utils.long2Date(d)));
    }
}
