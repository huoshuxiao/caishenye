package com.sun.caishenye.octopus.stock.agent.api;

import com.google.gson.Gson;
import com.sun.caishenye.octopus.stock.domain.ShHqDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * RestTemplate 采集 上海证券交易所
 */
@Component
@Slf4j
public class ShRestTemplate {

    // 实时行情 上海证券交易所
    // http://yunhq.sse.com.cn:32041//v1/sh1/line/600000?callback=jQuery11240024167803351734296_1583823993285&begin=0&end=-1&select=time%2Cprice%2Cvolume&_=1583823993299
    protected static final String SH_HQ_BASE_URL = "http://yunhq.sse.com.cn:32041//v1/sh1/line/{companyCode}?callback=jQuery{random}_{random2}&begin=0&end=-1&select=time,price,volume&_={random3}";

    // 历史行情 上海证券交易所
    // http://yunhq.sse.com.cn:32041//v1/sh1/dayk/600000?callback=jQuery112404567523489726468_1585101083795&select=date%2Copen%2Chigh%2Clow%2Cclose%2Cvolume&begin=-3650&end=-1&_=1585101083815
    protected static final String SH_HHQ_BASE_URL = "http://yunhq.sse.com.cn:32041//v1/sh1/dayk/{companyCode}?callback=jQuery{random}_{random2}&select=date,open,high,low,close,volume&begin=-{days}&end=-1&_={random3}";

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public ShRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 历史行情
    public ShHqDomain getHhqData(StockDomain stockDomain, long days) {

        log.debug("ShRestTemplate call hhq request params :: {}", stockDomain);
        ShHqDomain shHqDomain = new ShHqDomain();
        try {

            // call rest service
            String response = restTemplate.getForObject(SH_HHQ_BASE_URL, String.class, builderHhqDataUrl(stockDomain, days));
            log.debug("ShRestTemplate hhq response string :: {}", response);
            response = StringUtils.substringBetween(response, "(", ")");
            log.debug("ShRestTemplate hhq response :: {}", response);

            Gson gson = new Gson();
            shHqDomain = gson.fromJson(response, ShHqDomain.class);
            log.debug("ShRestTemplate call hhq response value :: {}", shHqDomain);
        } catch (RestClientException e) {
            log.error("hhq "+ stockDomain.getCompanyCode() + " " + e.getMessage());
        }
        return shHqDomain;
    }

    private Map<String, Object> builderHhqDataUrl(StockDomain stockDomain, long days) {
        Map<String, Object> params = builderHqDataUrl(stockDomain);
        params.put("days", days);
        return params;
    }

    // 实时行情
    @Async
    public CompletableFuture<ShHqDomain> getHqData(StockDomain stockDomain) {

        log.debug("ShRestTemplate call hq request params :: {}", stockDomain);
        ShHqDomain shHqDomain = new ShHqDomain();
        try {

            // call rest service
            String response = restTemplate.getForObject(SH_HQ_BASE_URL, String.class, builderHqDataUrl(stockDomain));
            response = StringUtils.substringBetween(response, "(", ")");

            Gson gson = new Gson();
            shHqDomain = gson.fromJson(response, ShHqDomain.class);
            log.debug("ShRestTemplate call hq response value :: {}", shHqDomain);
        } catch (RestClientException e) {
            log.error("hq "+ stockDomain.getCompanyCode() + " " + e.getMessage());
        }
        return CompletableFuture.completedFuture(shHqDomain);
    }

    private Map<String, Object> builderHqDataUrl(StockDomain stockDomain) {

        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextLong());
        params.put("random2", RandomUtils.nextInt());
        params.put("random3", RandomUtils.nextInt());
        return params;
    }
}
