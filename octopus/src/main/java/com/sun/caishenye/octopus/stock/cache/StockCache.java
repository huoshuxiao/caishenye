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

    private String XQ_URL = "http://xueqiu.com/snowman/S/SZ000001/detail";

    @Value("${cookie.xq}")
    private String cookie;

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplateText;

    @Cacheable(value = "ehcache_1H")
    public String getXQCookies() {
        ResponseEntity<String> response = restTemplateText.getForEntity(XQ_URL, String.class);
        List<String > cookies = response.getHeaders().get("Set-Cookie");
        String xq = String.join(";", cookies);
        log.info("cookies :: 1 {}", xq);

        if (!xq.contains("token")) {
            xq = cookie;
        }
        log.info("cookies :: 2 {}", xq);
        return xq;
    }
}
