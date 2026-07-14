package com.sun.caishenye.octopus.stock.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class StockCache {

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplateText;

    private String XQ_URL = "http://xueqiu.com/snowman/S/SZ000001/detail";

    @Value("${cookie.xq}")
    private String xqCookie;

    private String EM_URL = "http://data.eastmoney.com";

    @Value("${cookie.em}")
    private String emCookie;

    @Cacheable(value = "ehcache_1H_XQ")
    public String getXQCookies() {
        ResponseEntity<String> response = restTemplateText.getForEntity(XQ_URL, String.class);
        List<String > cookies = response.getHeaders().get("Set-Cookie");
        String xq = String.join(";", cookies);
        log.info("XQ cookies :: 1 {}", xq);

        if (!xq.contains("token")) {
            xq = xqCookie;
        }
        log.info("XQ cookies :: 2 {}", xq);
        return xq;
    }

    @Cacheable(value = "ehcache_1H_EM")
    public String getEMCookies() {
        ResponseEntity<String> response = restTemplateText.getForEntity(EM_URL, String.class);
        List<String > cookies = response.getHeaders().get("Set-Cookie");
        String xq = emCookie;
        if (cookies == null) {
            return xq;
        } else {
            xq = String.join(";", cookies);
            log.info("EM cookies :: 1 {}", xq);
            if (!xq.contains("token")) {
                xq = emCookie;
            }
            log.info("EM cookies :: 2 {}", xq);
        }
        return xq;
    }
}
