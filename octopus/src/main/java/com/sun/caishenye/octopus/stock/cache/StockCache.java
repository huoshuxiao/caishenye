package com.sun.caishenye.octopus.stock.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class StockCache {

    private String XQ_URL = "http://www.xueqiu.com";

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplateText;

    @Cacheable(value = "getXQCookies")
    public String getXQCookies() {
        ResponseEntity<String> response = restTemplateText.getForEntity(XQ_URL, String.class);
        List<String > cookies = response.getHeaders().get("Set-Cookie");
        String xq = String.join(";", cookies);
        log.info("cookies :: {}", xq);
        return xq;
    }
}
