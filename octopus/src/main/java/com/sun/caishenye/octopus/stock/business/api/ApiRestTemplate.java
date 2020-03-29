package com.sun.caishenye.octopus.stock.business.api;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
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
            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
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
                getHhqForObject(stockDomain).get().getHqs().parallelStream().forEach(t -> {
                    if (t[0].equals(getDay(stockDomain))) {
                        isOK.set(true);
                        // 收盘日
                        hhqDomain.setDay(getDay(stockDomain));
                        // 收盘价
                        hhqDomain.setPrice(t[2]);
                        return;
                    }
                });
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