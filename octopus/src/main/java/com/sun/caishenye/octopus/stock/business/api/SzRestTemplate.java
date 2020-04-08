package com.sun.caishenye.octopus.stock.business.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.SzHqDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // 历史行情 深圳证券交易所
    // http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC=000001&txtBeginDate=2020-03-24&txtEndDate=2020-03-24&radioClass=00%2C20%2C30&txtSite=all&random=0.5182614190145614
    protected final String SZ_HHQ_BASE_URL = "http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC={companyCode}&txtBeginDate={beginDate}&txtEndDate={endDate}&radioClass=00,20,30&txtSite=all&random={random}";

    @Autowired
    private RestTemplate restTemplate;

    // 历史行情
    public SzHqDomain getHhqForObject(StockDomain stockDomain) {

        log.debug("SzRestTemplate call hhq request params :: {}", stockDomain);
        SzHqDomain hqDomain = new SzHqDomain();
        try {
            // call rest service
            List<Map<String, List<Map<String, String>>>> responseList = restTemplate.getForObject(SZ_HHQ_BASE_URL, List.class, hhqUrlBuilder(stockDomain));
            Map<String, List<Map<String, String>>> mapList = responseList.get(0);
            List<Map<String, String>> dataList = mapList.get("data");
            if (dataList.size() == 0) {
                return null;
            }
            log.debug("SzRestTemplate call hhq response :: {}", dataList.get(0).toString());

            Map<String, String> dataMap = dataList.get(0);
            if (dataMap == null) {
                return null;
            } else {
                log.debug("SzRestTemplate call hhq response :: {}", dataMap.toString());
                // 收盘价
                hqDomain.setPrice(dataMap.get("ss"));
                log.debug("SzRestTemplate call hhq response value :: {}", hqDomain);
            }
        } catch (HttpClientErrorException e) {
            log.error("hhq "+ stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        }
        return hqDomain;
    }

    private Map<String, Object> hhqUrlBuilder(StockDomain stockDomain) {

        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextInt());
        params.put("beginDate", getDay(stockDomain));
        params.put("endDate", getDay(stockDomain));
        return params;
    }

    private String getDay(StockDomain stockDomain) {
        return "--".equals(stockDomain.getSbDomain().getDividendDate()) ? stockDomain.getSbDomain().getRegistrationDate() : stockDomain.getSbDomain().getDividendDate();
    }

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
            log.error("hq "+ stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
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
