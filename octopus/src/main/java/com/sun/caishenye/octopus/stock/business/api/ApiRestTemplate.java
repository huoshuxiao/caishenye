package com.sun.caishenye.octopus.stock.business.api;

import com.google.gson.Gson;
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

/**
 * RestTemplate 采集 (call api)
 */
@Component
@Slf4j
public class ApiRestTemplate {

    // 历史行情 金融界
    // http://flashdata2.jrj.com.cn/history/js/share/601628/other/dayk_ex.js?random=1585145121921
    protected final String JRJ_HHQ_URL = "http://flashdata2.jrj.com.cn/history/js/share/{companyCode}/other/dayk_ex.js?random={random}";

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
}
