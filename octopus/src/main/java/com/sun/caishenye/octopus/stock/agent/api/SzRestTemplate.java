package com.sun.caishenye.octopus.stock.agent.api;

import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.SzHqDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    // 实时行情(分时图) 深圳证券交易所
    // http://www.szse.cn/api/market/ssjjhq/getTimeData?random=0.8019259119284983&marketId=1&code=000001
//    protected static final String SZ_HQ_BASE_URL = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random={random}&marketId=1&code={companyCode}";
    protected static final String SZ_HQ_BASE_URL = "http://big5.szse.cn/site/cht/www.szse.cn/api/market/ssjjhq/getTimeData?random={random}&marketId=1&code={companyCode}";

    // 历史行情(日次) 深圳证券交易所
    // http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC=000001&txtBeginDate=2020-03-24&txtEndDate=2020-03-24&radioClass=00%2C20%2C30&txtSite=all&random=0.5182614190145614
    protected static final String SZ_HHQ_BASE_URL = "http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC={companyCode}&txtBeginDate={beginDate}&txtEndDate={endDate}&radioClass=00,20,30&txtSite=all&random={random}";
//    protected static final String SZ_HHQ_BASE_URLURL = "http://big5.szse.cn/site/cht/www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC={companyCode}&txtBeginDate={beginDate}&txtEndDate={endDate}&radioClass=00,20,30&txtSite=all&random={random}";

    @Autowired
    private RestTemplate restTemplate;

    // 历史行情
    public SzHqDomain getHhqData(StockDomain stockDomain, String date) {

        log.debug("SzRestTemplate call hhq request params :: {}", stockDomain);
        SzHqDomain hqDomain = new SzHqDomain();
        try {
            List<Map<String, List<Map<String, String>>>> responseList = restTemplate.getForObject(SZ_HHQ_BASE_URL, List.class, builderHhqDataUrl(stockDomain, date));
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
                log.debug("SzRestTemplate call hhq response :: {}", dataMap);
                // 收盘价
                hqDomain.setPrice(dataMap.get("ss"));
                log.debug("SzRestTemplate call hhq response value :: {}", hqDomain);
            }
        } catch (RestClientException e) {
            log.error("sz hhq retry {} :: {}", stockDomain.getCompanyCode(), e.getMessage());
            // 访问异常 retry
            getHhqData(stockDomain, date);
        }
        return hqDomain;
    }

    private Map<String, Object> builderHhqDataUrl(StockDomain stockDomain, String date) {

        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextInt());
        if (StringUtils.isEmpty(date)) {
            params.put("beginDate", stockDomain.getSbDomain().getRegistrationDate());
            params.put("endDate", stockDomain.getSbDomain().getRegistrationDate());
        } else {
            params.put("beginDate", date);
            params.put("endDate", date);
        }
        return params;
    }

    // 实时行情
    @Async
    public CompletableFuture<SzHqDomain> getHqData(StockDomain stockDomain) {

        log.debug("SzRestTemplate call hq request params :: {}", stockDomain);
        SzHqDomain hqDomain = new SzHqDomain();
        try {
            // call rest service
            hqDomain = restTemplate.getForObject(SZ_HQ_BASE_URL, SzHqDomain.class, builderHqDataUrl(stockDomain));
            log.debug("SzRestTemplate call hq response value :: {}", hqDomain);
        } catch (RestClientException e) {
            log.error("hq "+ stockDomain.getCompanyCode() + " " + e.getMessage());
        }
        return CompletableFuture.completedFuture(hqDomain);
    }

    private Map<String, Object> builderHqDataUrl(StockDomain stockDomain) {

        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextInt());
        return params;
    }
}