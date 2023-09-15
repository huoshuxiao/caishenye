package com.sun.caishenye.octopus;

import com.sun.caishenye.octopus.common.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// If your test is @Transactional, it rolls back the transaction at the end of each test method by default.
// However, as using this arrangement with either RANDOM_PORT or DEFINED_PORT implicitly provides a real servlet environment
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// If you are using JUnit 4, don’t forget to also add @RunWith(SpringRunner.class) to your test
//@RunWith(SpringRunner.class)
class OctopusApplicationTests {

    @Test
    void threads() {
        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println(LocalDate.now());
    }

    @Test
    void test001() {
//        123456789012
//        411565620700
//        四千一百一十五亿六千五百六十二万〇七百
        long m = 0L;
        LocalDateTime startTime = LocalDateTime.now();
        for (int i = 1; i <= 40993; i++) {
            for (int j = 1; j <= 10039900; j++) {
                m++;
                System.out.println(m + " " + String.valueOf(m).length());
            }
        }
        LocalDateTime endTime = LocalDateTime.now();
        System.out.println(ChronoUnit.MINUTES.between(startTime, endTime));
    }

}
