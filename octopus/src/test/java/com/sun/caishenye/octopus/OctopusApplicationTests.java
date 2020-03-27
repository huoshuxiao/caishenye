package com.sun.caishenye.octopus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

// If your test is @Transactional, it rolls back the transaction at the end of each test method by default.
// However, as using this arrangement with either RANDOM_PORT or DEFINED_PORT implicitly provides a real servlet environment
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// If you are using JUnit 4, don’t forget to also add @RunWith(SpringRunner.class) to your test
//@RunWith(SpringRunner.class)
class OctopusApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println(LocalDate.now());
    }

}
