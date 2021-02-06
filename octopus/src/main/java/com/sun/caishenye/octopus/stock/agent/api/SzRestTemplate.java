package com.sun.caishenye.octopus.stock.agent.api;

import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.SzHqDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * RestTemplate 采集 深圳证券交易所
 */
@Component
@Slf4j
public class SzRestTemplate {

    // 基础数据 深圳证券交易所
    // http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1110&TABKEY=tab1&txtDMorJC=000001&random=0.3891717838296198
    private static final String SZ_BASE_DATA_URL = "http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1110&TABKEY=tab1&txtDMorJC={companyCode}&random={random}";

    // 实时行情 深圳证券交易所
    // http://www.szse.cn/api/market/ssjjhq/getTimeData?random=0.8019259119284983&marketId=1&code=000001
    protected static final String SZ_HQ_BASE_URL = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random={random}&marketId=1&code={companyCode}";

    // 历史行情 深圳证券交易所
    // http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC=000001&txtBeginDate=2020-03-24&txtEndDate=2020-03-24&radioClass=00%2C20%2C30&txtSite=all&random=0.5182614190145614
    protected static final String SZ_HHQ_BASE_URL = "http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1815_stock&TABKEY=tab1&txtDMorJC={companyCode}&txtBeginDate={beginDate}&txtEndDate={endDate}&radioClass=00,20,30&txtSite=all&random={random}";

    @Autowired
    private RestTemplate restTemplate;

    // 基础数据
    public StockDomain getBaseData(String companyCode) {

        log.debug("SzRestTemplate call base request params :: {}", companyCode);

        List<Map<String, List<Map<String, String>>>> baseDataList = restTemplate.getForObject(SZ_BASE_DATA_URL, List.class, builderBaseDataUrl(companyCode));
        Map<String, List<Map<String, String>>> baseDataMap = baseDataList.stream().findFirst().get();
        List<Map<String, String>> dataList = baseDataMap.get("data");
        if (dataList.isEmpty()) {
            return null;
        }

        log.debug("SzRestTemplate call base response :: {}", dataList.stream().findFirst().get().toString());
        Map<String, String> dataMap = dataList.stream().findFirst().get();
        if (dataMap == null) {
            return null;
        }

        log.debug("SzRestTemplate call base response :: {}", dataMap.toString());
        StockDomain baseData = new StockDomain();
        // 公司代码
        baseData.setCompanyCode(dataMap.get("agdm"));
        // 上市日期
        baseData.setListingDate(dataMap.get("agssrq"));

        log.debug("SzRestTemplate call base response value :: {}", baseData);
        return baseData;
    }

    private Map<String, Object> builderBaseDataUrl(String companyCode) {

        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", companyCode);
        params.put("random", RandomUtils.nextInt());
        return params;
    }

    // 历史行情
    public SzHqDomain getHhqData(StockDomain stockDomain) {

        log.debug("SzRestTemplate call hhq request params :: {}", stockDomain);
        SzHqDomain hqDomain = new SzHqDomain();
        try {
            // call rest service
            List<Map<String, List<Map<String, String>>>> responseList = restTemplate.getForObject(SZ_HHQ_BASE_URL, List.class, builderHhqDataUrl(stockDomain));
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

    private Map<String, Object> builderHhqDataUrl(StockDomain stockDomain) {

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
    public CompletableFuture<SzHqDomain> getHqData(StockDomain stockDomain) {

        log.debug("SzRestTemplate call hq request params :: {}", stockDomain);
        SzHqDomain hqDomain = new SzHqDomain();
        try {
            // call rest service
            hqDomain = restTemplate.getForObject(SZ_HQ_BASE_URL, SzHqDomain.class, builderHqDataUrl(stockDomain));
            log.debug("SzRestTemplate call hq response value :: {}", hqDomain);
        } catch (HttpClientErrorException e) {
            log.error("hq "+ stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
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
