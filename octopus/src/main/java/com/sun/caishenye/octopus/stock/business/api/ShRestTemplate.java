//package com.sun.caishenye.octopus.stock.business.api;
//
//import com.sun.caishenye.octopus.fund.domain.FundDomain;
//import com.sun.caishenye.octopus.fund.domain.MorningStarDetailDomain;
//import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
//import com.sun.caishenye.octopus.stock.domain.PageHelpDomain;
//import com.sun.caishenye.octopus.stock.domain.SseQueryDomain;
//import com.sun.caishenye.octopus.stock.domain.StockDomain;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.Clock;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
///**
// * RestTemplate 采集 上海证券交易所
// */
//@Component
//@Slf4j
//public class ShRestTemplate {
//
//    /* http://query.sse.com.cn/security/stock/getStockListData2.do?&jsonCallBack=jsonpCallback40914&isPagination=true&stockCode=&csrcCode=&areaName=&stockType=1&pageHelp.cacheSize=1&pageHelp.beginPage=1&pageHelp.pageSize=25&pageHelp.pageNo=1&pageHelp.endPage=11&_=1584768432591 */
//    protected final String BASE_DATA_URL = "http://query.sse.com.cn/security/stock/getStockListData2.do?";
//    protected final String QUERY_URL = "jsonCallBack={jsonCallBack}&isPagination=true&stockCode=&csrcCode=&areaName=&stockType=1" +
//            "&pageHelp.cacheSize={cacheSize}&pageHelp.beginPage={beginPage}&pageHelp.pageSize={pageSize}&pageHelp.pageNo={pageNo}&pageHelp.endPage={endPage}&_={times}";
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    public ShRestTemplate(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    @Async
//    public CompletableFuture<SseQueryDomain> getBaseForObject(PageHelpDomain pageHelpDomain) {
//
//        String url = BASE_DATA_URL + QUERY_URL;
//        // call rest service
//        SseQueryDomain stockDomain = restTemplate.getForObject(url, SseQueryDomain.class, urlBuilder(pageHelpDomain));
//
//        log.debug("ShangZhengRestTemplate call response value :: {}", stockDomain);
//
//        return CompletableFuture.completedFuture(stockDomain);
//    }
//
//    protected Map<String, Object> urlBuilder(PageHelpDomain pageHelpDomain) {
//
//        String randomid = String.valueOf(RandomUtils.nextInt()).substring(0, 5);
//        Map<String, Object> params = new HashMap<>();
//        params.put("jsonCallBack", "jsonpCallback" + randomid);
//        params.put("cacheSize", pageHelpDomain.getCacheSize());
//        params.put("beginPage", pageHelpDomain.getBeginPage());
//        params.put("pageSize", pageHelpDomain.getPageSize());
//        params.put("pageNo", pageHelpDomain.getPageNo());
//        params.put("endPage", pageHelpDomain.getEndPage());
//        params.put("times", System.currentTimeMillis());
//        return params;
//    }
//}
