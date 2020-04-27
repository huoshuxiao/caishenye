package com.sun.caishenye.octopus.stock.business.api;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.ShHqDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.SzHqDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RestTemplate 采集 (call api)
 */
@Component
@Slf4j
public class ApiRestTemplate {

    // 历史行情 金融界
    // http://flashdata2.jrj.com.cn/history/js/share/601628/other/dayk_ex.js?random=1585145121921
    protected final String JRJ_HHQ_URL = "http://flashdata2.jrj.com.cn/history/js/share/{companyCode}/other/dayk_ex.js?random={random}";

    // 历史行情 搜狐
    // http://q.stock.sohu.com/hisHq?code=cn_603999&start=20091126&end=20200325&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.028961481283250157&0.037908320278956964
    protected final String SOHU_HHQ_URL = "http://q.stock.sohu.com/hisHq?code=cn_{companyCode}&start={startDay}&end={endDay}&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r={random1}&{random2}";

    @Autowired
    private ShRestTemplate shRestTemplate;

    @Autowired
    private SzRestTemplate szRestTemplate;

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplate;

    // 历史行情
    @Async
    public CompletableFuture<DayLineDomain> getHhqForObject(StockDomain stockDomain) {
        log.debug("call hhq request params :: {}", stockDomain);
        DayLineDomain hhqDomain = new DayLineDomain();
        try {

            // call rest service
            String response = restTemplate.getForObject(JRJ_HHQ_URL, String.class, hhqUrlBuilder(stockDomain));
            log.debug("call hhq response string :: {}", response);
            // 结构化返回值，对返回值进行fmt
            response = StringUtils.removeStart(response, "var s_d_ex_" + stockDomain.getCompanyCode() + "=");
            response = StringUtils.substringBefore(response,"\"factor\"");
            response = response.replace("]],","]]}");

            log.debug("call hhq response :: {}", response);
            Gson gson = new Gson();
            hhqDomain = gson.fromJson(response, DayLineDomain.class);

            log.debug("call hhq response value :: {}", hhqDomain);
        } catch (HttpClientErrorException e) {
            log.warn(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        } catch (JsonSyntaxException je) {
            hhqUrlBuilder(stockDomain);
            log.error(hhqUrlBuilder(stockDomain) + " " + je);
        }
        return CompletableFuture.completedFuture(hhqDomain);
    }

    private Map<String, Object> hhqUrlBuilder(StockDomain stockDomain) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextInt());
        return params;
    }

    // 历史行情(指定日期)
    public DayLineDomain getHhqByDateForObject(StockDomain stockDomain) {
        log.debug("call hhq request params :: {}", stockDomain);
        // call rest service
        String response = restTemplate.getForObject(SOHU_HHQ_URL, String.class, hhqUrlBuilderWithSohu(stockDomain));
        log.debug("call hhq response string :: {}", response);
        // 结构化返回值，对返回值进行fmt
        response = StringUtils.substringBetween(response, "(",")");
        log.debug("call hhq response :: {}", response);

        // not found, call jrj api
        if ("{}".equals(response)) {
            DayLineDomain hhqDomain = new DayLineDomain();
            AtomicBoolean isOK = new AtomicBoolean(false);
            try {
                String day = getDay(stockDomain);
                DayLineDomain tDayLineDomain = getHhqForObject(stockDomain).get();
                if (tDayLineDomain.getSummary() == null) {return null;}
                tDayLineDomain.getHqs().parallelStream().forEach(t -> {
                    if (t[0].equals(day)) {
                        isOK.set(true);
                        // 收盘日
                        hhqDomain.setDay(day);
                        // 收盘价
                        hhqDomain.setPrice(t[2]);
                        return;
                    }
                });

                // 从 证券交易所 取数据
                if (!isOK.get()) {

                    if (tDayLineDomain.getSummary().getId().contains(Constants.EXCHANGE_SZ.getString())) {

                        // call SzRestTemplate
                        SzHqDomain hqDomain = szRestTemplate.getHhqForObject(stockDomain);
                        if (hqDomain != null) {
                            // 收盘价
                            hhqDomain.setPrice(hqDomain.getPrice());
                            isOK.set(true);
                        }

                    } else {

                        // call ShRestTemplate
                        long days = ChronoUnit.DAYS.between(LocalDate.of(Integer.valueOf(day.substring(0, 4)), Integer.valueOf(day.substring(4, 6)), Integer.valueOf(day.substring(6, 8))),
                                LocalDate.now());
                        ShHqDomain shHqDomain = shRestTemplate.getHhqForObject(stockDomain, days);
                        if (shHqDomain != null) {
                            // 收盘价
                            shHqDomain.getKline().stream().forEach(t -> {
                                if (day.equals(t[0])) {
                                    hhqDomain.setPrice(t[3]);
                                    isOK.set(true);
                                }
                            });
                        }
                    }
                    // 收盘日
                    hhqDomain.setDay(day);
                }

            } catch (InterruptedException | ExecutionException e) {
                log.error("call getHhqForObject error " + e);
                return null;
            }
            return isOK.get() == true ? hhqDomain : null;
        }

        DayLineDomain hhqDomain = null;
        JSONArray jsonArray = JSONArray.parseArray(response);
        if (jsonArray.size() > 0) {
            Gson gson = new Gson();
            log.debug("call hhq response jsonarray value :: {}", jsonArray.get(0).toString());
            hhqDomain = gson.fromJson(jsonArray.get(0).toString(), DayLineDomain.class);
            // 收盘日
            hhqDomain.setDay(Utils.formatDate2String(hhqDomain.getHq().get(0)[0]));
            // 收盘价
            hhqDomain.setPrice(hhqDomain.getHq().get(0)[2]);
        }
        log.debug("call hhq response value :: {}", hhqDomain);
        return hhqDomain;
    }

    private Map<String, Object> hhqUrlBuilderWithSohu(StockDomain stockDomain) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("startDay", getDay(stockDomain));
        params.put("endDay", getDay(stockDomain));
        params.put("random1", RandomUtils.nextInt());
        params.put("random2", RandomUtils.nextInt());
        return params;
    }

    private String getDay(StockDomain stockDomain) {
        return Utils.formatDate2String("--".equals(stockDomain.getSbDomain().getDividendDate()) ? stockDomain.getSbDomain().getRegistrationDate() : stockDomain.getSbDomain().getDividendDate());
    }
}