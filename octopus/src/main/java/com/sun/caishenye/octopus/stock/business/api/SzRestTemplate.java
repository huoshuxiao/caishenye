package com.sun.caishenye.octopus.stock.business.api;

import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.SzHqDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * RestTemplate 采集 深圳证券交易所
 */
@Component
@Slf4j
public class SzRestTemplate {

    // 实时行情 深圳证券交易所
    // http://www.szse.cn/api/market/ssjjhq/getTimeData?random=0.8019259119284983&marketId=1&code=000001
    protected final String SZ_HQ_BASE_URL = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random={random}&marketId=1&code={companyCode}";

//    // 历史行情 深圳证券交易所
//    // http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC=000001&txtBeginDate=2020-03-24&txtEndDate=2020-03-24&radioClass=00%2C20%2C30&txtSite=all&random=0.5182614190145614
//    protected final String SZ_HHQ_BASE_URL = "http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC={companyCode}&txtBeginDate={beginDate}&txtEndDate={endDate}&radioClass=00,20,30&txtSite=all&random={random}";

    @Autowired
    private RestTemplate restTemplate;

//    // 历史行情
//    @Async
//    public CompletableFuture<SzHqDomain> getHhqForObject(StockDomain stockDomain) {
//
//        log.debug("SzRestTemplate call hhq request params :: {}", stockDomain);
//        SzHqDomain hqDomain = new SzHqDomain();
//        try {
//            // call rest service
//            String str = restTemplate.getForObject(SZ_HHQ_BASE_URL, String.class, hhqUrlBuilder(stockDomain));
//            log.debug("SzRestTemplate call hhq response value :: {}", str);
//        } catch (HttpClientErrorException e) {
//            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
//        }
//        return CompletableFuture.completedFuture(hqDomain);
//    }
//
//    private Map<String, Object> hhqUrlBuilder(StockDomain stockDomain) {
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("companyCode", stockDomain.getCompanyCode());
//        params.put("random", RandomUtils.nextInt());
//        params.put("beginDate", "");
//        params.put("endDate", "");
//        return params;
//    }

    // 实时行情
    @Async
    public CompletableFuture<SzHqDomain> getHqForObject(StockDomain stockDomain) {

        log.debug("SzRestTemplate call hq request params :: {}", stockDomain);
        SzHqDomain hqDomain = new SzHqDomain();
        try {
            // call rest service
            hqDomain = restTemplate.getForObject(SZ_HQ_BASE_URL, SzHqDomain.class, hqUrlBuilder(stockDomain));
            log.debug("SzRestTemplate call hq response value :: {}", hqDomain);
        } catch (HttpClientErrorException e) {
            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        }
        return CompletableFuture.completedFuture(hqDomain);
    }

    private Map<String, Object> hqUrlBuilder(StockDomain stockDomain) {

        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextInt());
        return params;
    }
}
