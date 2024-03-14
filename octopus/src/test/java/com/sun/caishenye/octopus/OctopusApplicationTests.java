package com.sun.caishenye.octopus;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

// If your test is @Transactional, it rolls back the transaction at the end of each test method by default.
// However, as using this arrangement with either RANDOM_PORT or DEFINED_PORT implicitly provides a real servlet environment
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
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

    @Test
    void test002() {
        String s = "cookiesu=161709307807188; u=161709307807188; Hm_lvt_1db88642e346389874251b5a1eded6e3=1709307811; device_id=0ca53bf1f1e31bba3f3aa5c63304d08a; s=br1655z2xv; xq_a_token=52dfb79aed5f2cdd1e7c2cfc56054ac1f5b77fc3; xqat=52dfb79aed5f2cdd1e7c2cfc56054ac1f5b77fc3; xq_r_token=e20d82fd7b432e0f32c54be5af4c28605e8c191f; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTcxMjM2NDQwMSwiY3RtIjoxNzEwMDcyMjk3NTgwLCJjaWQiOiJkOWQwbjRBWnVwIn0.hPWxb0bNH0_LEXJQMD2PuoBspBMFQbWUXBi-BETbSrnnZE_5vjHYHqzr2qU5KnnPpYA4Wbu_UNzAPSmpi3vBzQqWJMxCx_hd0mJIXskRa7oc5M8tPnLz80CtUQBwsEP5xzU1lEL4K_KoP2SkxGp6ZcH_Mhx_UqItzhEFjbzTNboHtV9ztr3jerwtRIvk98aQJSirrfGYvnnnZ_OIvKMbVfiwvS0py_HgM0FjK0AhqmJW9SpyApEJ_aD4V7J-3jwq6vCNhI4ay18vYIyDNLXCkV7z8FdgRT5AdvxtOfdXucbA1rT3Rb7rLa9dx0JM08HfoUvDM9NhH3-IDyLsgeiOJg; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1710072500";
        Arrays.stream(s.split(";")).sorted().forEach(t -> System.out.println(StringUtils.trim(t)));
    }

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplateText;
    @Test
    void test003() {
        String XQ_URL = "http://www.xueqiu.com";
        ResponseEntity<String> response = restTemplateText.getForEntity(XQ_URL, String.class);
        List<String > cookies = response.getHeaders().get("Set-Cookie");
        cookies.forEach(System.out::println);
    }

}
