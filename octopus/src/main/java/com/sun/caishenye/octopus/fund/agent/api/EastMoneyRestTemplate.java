package com.sun.caishenye.octopus.fund.agent.api;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * RestTemplate 采集天天基金网数据
 */
@Component
@Slf4j
public class EastMoneyRestTemplate {

    // 基础数据
    // http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=&gs=0&sc=qjzf&st=desc&sd=1990-05-03&ed=2021-05-03&qdii=&tabSubtype=,,,,,&pi=1&pn=50000&dx=0&v=0.5407120213816268
    private static final String BASE_DATA_URL = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=&gs=0&sc=qjzf&st=desc&sd={startDate}&ed={endDate}&qdii=&tabSubtype=,,,,,&pi=1&pn=50000&dx=0&v=0.5407120213816268";

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public EastMoneyRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getBaseData(Integer year) {

        log.debug("EastMoneyRestTemplate call base request start");

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Referer", "http://fund.eastmoney.com/data/fundranking.html");
        headers.set("Host", "fund.eastmoney.com");
        headers.set("Cookie", "ASP.NET_SessionId=oodjjkqu1tqnzkiw5u52232v");

        // build the request
        HttpEntity<String> request = new HttpEntity<>(headers);

        // call rest service
        ResponseEntity<String> responseEntity = restTemplate.exchange(builderBaseDataUrl(year), HttpMethod.GET, request, String.class);
        String response = responseEntity.getBody();
        log.debug("EastMoneyRestTemplate base response string :: {}", response);

        // 结构化返回值，对返回值进行fmt
        response = StringUtils.removeStart(response, "var rankData = ");
        response = StringUtils.removeEnd(response, ";");
        log.debug("EastMoneyRestTemplate call base data response :: {}", response);

        // 获取数据部分
        Gson gson = new Gson();
        Map<String, Object> responseMap = gson.fromJson(response, Map.class);
        List<String> data = (List<String>)responseMap.get("datas");
        log.debug("EastMoneyRestTemplate call base data response :: {}", data);

        return data;
    }

    private String builderBaseDataUrl(Integer year) {
        String url = BASE_DATA_URL.replace("{startDate}", LocalDate.now().minusYears(year).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        url = url.replace("{endDate}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return url;
    }
}
