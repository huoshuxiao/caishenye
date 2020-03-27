package com.sun.caishenye.octopus.stock.business.api;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * RestTemplate 采集 上海证券交易所
 */
@Component
@Slf4j
public class ShRestTemplate {

    /* http://query.sse.com.cn/security/stock/getStockListData2.do?&jsonCallBack=jsonpCallback40914&isPagination=true&stockCode=&csrcCode=&areaName=&stockType=1&pageHelp.cacheSize=1&pageHelp.beginPage=1&pageHelp.pageSize=25&pageHelp.pageNo=1&pageHelp.endPage=11&_=1584768432591 */
//    protected final String BASE_DATA_URL = "http://query.sse.com.cn/security/stock/getStockListData2.do?";
//    protected final String QUERY_URL = "jsonCallBack={jsonCallBack}&isPagination=true&stockCode=&csrcCode=&areaName=&stockType=1" +
//            "&pageHelp.cacheSize={cacheSize}&pageHelp.beginPage={beginPage}&pageHelp.pageSize={pageSize}&pageHelp.pageNo={pageNo}&pageHelp.endPage={endPage}&_={times}";

    // 实时行情 上海证券交易所
    // http://yunhq.sse.com.cn:32041//v1/sh1/line/600000?callback=jQuery11240024167803351734296_1583823993285&begin=0&end=-1&select=time%2Cprice%2Cvolume&_=1583823993299
    protected final String SH_HQ_BASE_URL = "http://yunhq.sse.com.cn:32041//v1/sh1/line/{companyCode}?callback=jQuery{random}_{random2}&begin=0&end=-1&select=time,price,volume&_={random3}";

//    // 历史行情 上海证券交易所
//    // http://yunhq.sse.com.cn:32041//v1/sh1/dayk/600000?callback=jQuery112404567523489726468_1585101083795&select=date%2Copen%2Chigh%2Clow%2Cclose%2Cvolume&begin=-3650&end=-1&_=1585101083815
//    protected final String SH_HHQ_BASE_URL = "http://yunhq.sse.com.cn:32041//v1/sh1/dayk/{companyCode}?callback=jQuery{random}_{random2}&select=date,open,high,low,close,volume&begin=-"+ Constants.HHQ_DATES.getInteger() +"&end=-1&_={random3}";

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public ShRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

//    // 历史行情
//    @Async
//    public CompletableFuture<ShHqDomain> getHhqForObject(StockDomain stockDomain) {
//
//        log.debug("ShRestTemplate call hhq request params :: {}", stockDomain);
//        ShHqDomain shHqDomain = new ShHqDomain();
//        try {
//
//            // call rest service
//            String response = restTemplate.getForObject(SH_HHQ_BASE_URL, String.class, hhqUrlBuilder(stockDomain));
//            response = StringUtils.substringBetween(response, "(", ")");
//
//            Gson gson = new Gson();
//            shHqDomain = gson.fromJson(response, ShHqDomain.class);
//            log.debug("ShRestTemplate call hhq response value :: {}", shHqDomain);
//        } catch (HttpClientErrorException e) {
//            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
//        }
//        return CompletableFuture.completedFuture(shHqDomain);
//    }
//
//    private Map<String, Object> hhqUrlBuilder(StockDomain stockDomain) {
//        return hqUrlBuilder(stockDomain);
//    }

    // 实时行情
    @Async
    public CompletableFuture<ShHqDomain> getHqForObject(StockDomain stockDomain) {

        log.debug("ShRestTemplate call hq request params :: {}", stockDomain);
        ShHqDomain shHqDomain = new ShHqDomain();
        try {

            // call rest service
            String response = restTemplate.getForObject(SH_HQ_BASE_URL, String.class, hqUrlBuilder(stockDomain));
            response = StringUtils.substringBetween(response, "(", ")");

            Gson gson = new Gson();
            shHqDomain = gson.fromJson(response, ShHqDomain.class);
            log.debug("ShRestTemplate call hq response value :: {}", shHqDomain);
        } catch (HttpClientErrorException e) {
            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        }
        return CompletableFuture.completedFuture(shHqDomain);
    }

    private Map<String, Object> hqUrlBuilder(StockDomain stockDomain) {

        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextLong());
        params.put("random2", RandomUtils.nextInt());
        params.put("random3", RandomUtils.nextInt());
        return params;
    }

/*
    @Async
    public CompletableFuture<SseQueryDomain> getBaseForObject(PageHelpDomain pageHelpDomain) {

        String url = BASE_DATA_URL + QUERY_URL;
        // call rest service
        SseQueryDomain stockDomain = restTemplate.getForObject(url, SseQueryDomain.class, urlBuilder(pageHelpDomain));

        log.debug("ShangZhengRestTemplate call response value :: {}", stockDomain);

        return CompletableFuture.completedFuture(stockDomain);
    }

    protected Map<String, Object> urlBuilder(PageHelpDomain pageHelpDomain) {

        String randomid = String.valueOf(RandomUtils.nextInt()).substring(0, 5);
        Map<String, Object> params = new HashMap<>();
        params.put("jsonCallBack", "jsonpCallback" + randomid);
        params.put("cacheSize", pageHelpDomain.getCacheSize());
        params.put("beginPage", pageHelpDomain.getBeginPage());
        params.put("pageSize", pageHelpDomain.getPageSize());
        params.put("pageNo", pageHelpDomain.getPageNo());
        params.put("endPage", pageHelpDomain.getEndPage());
        params.put("times", System.currentTimeMillis());
        return params;
    }
 */
}
