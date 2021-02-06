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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * RestTemplate 采集 上海证券交易所
 */
@Component
@Slf4j
public class ShRestTemplate {

    // 基础数据 上海证券交易所
    // http://query.sse.com.cn/security/stock/getStockListData.do?&jsonCallBack=jsonpCallback44832&isPagination=true&stockCode=600000&csrcCode=&areaName=&stockType=1&pageHelp.cacheSize=1&pageHelp.beginPage=1&pageHelp.pageSize=25&pageHelp.pageNo=1&_=1612444691811
    private static final String SH_BASE_DATA_URL = "http://query.sse.com.cn/security/stock/getStockListData.do?&jsonCallBack=jsonpCallback{callback}&isPagination=true&stockCode={companyCode}&csrcCode=&areaName=&stockType={stockType}&pageHelp.cacheSize=1&pageHelp.beginPage=1&pageHelp.pageSize=25&pageHelp.pageNo=1&_={random}";

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

    // 基础数据
    public StockDomain getBaseData(String companyCode) {

        log.debug("ShRestTemplate call base request params :: {}", companyCode);

        // 600001/686868
        String stockType = "1";
        if (StringUtils.startsWith(companyCode, "68")) {
            // 科创板
            stockType = "8";
        }

        // call rest service
        String response = restTemplate.getForObject(SH_BASE_DATA_URL, String.class, builderBaseDataUrl(companyCode, stockType));
        log.debug("ShRestTemplate base response string :: {}", response);

        response = StringUtils.substringBetween(response, "(", ")");
        log.debug("ShRestTemplate base response :: {}", response);

        Gson gson = new Gson();
        Map<String, Object> mapData = gson.fromJson(response, Map.class);

        return null;
    }

    private Map<String, Object> builderBaseDataUrl(String companyCode, String stockType) {

        Map<String, Object> params = new HashMap<>();
        params.put("callback", (int)Math.floor(Math.random() * (100000 + 1)));
        params.put("companyCode", companyCode);
        params.put("stockType", stockType);
        params.put("random", Instant.now().toEpochMilli());
        return params;
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
        } catch (HttpClientErrorException e) {
            log.error("hhq "+ stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
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
        } catch (HttpClientErrorException e) {
            log.error("hq "+ stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
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
