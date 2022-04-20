package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.stock.agent.api.SzRestTemplate;
import com.sun.caishenye.octopus.stock.dao.SzDao;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.SzHqDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 深圳证券
 */
@Slf4j
@Service
public class SzService {

    @Autowired
    private SzRestTemplate restTemplate;

    @Autowired
    private SzDao szDao;

    public List<StockDomain> readBaseData() {

        return szDao.readBaseData();
    }

    // 实时行情 price
    public void hq(StockDomain stockDomain) throws ExecutionException, InterruptedException {

        // call rest service
        CompletableFuture<SzHqDomain> hqDomainCompletableFuture = CompletableFuture.supplyAsync(() -> restTemplate.getHqData(stockDomain)).get();
        SzHqDomain hqDomain = hqDomainCompletableFuture.get();
        if (StringUtils.isEmpty(hqDomain.getData().getNow())) {
            if (hqDomain.getDatetime().length() >= 10) {
                hqDomain = hhq(stockDomain, hqDomain.getDatetime().substring(0, 10));
            } else {
                log.error("hq {} 404 :: {}", stockDomain.getCompanyCode(), hqDomain);
                hqDomain = hhq(stockDomain, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } else {
            hqDomain.setPrice(hqDomain.getData().getNow() == null ? Constants.HQ_SUSPENSION.getString() : hqDomain.getData().getNow());
        }
        if (hqDomain == null) {
            hqDomain = new SzHqDomain();
            hqDomain.setPrice(Constants.HQ_SUSPENSION.getString());
        }
        stockDomain.setPrice(hqDomain.getPrice());
    }

    // 历史行情
    public SzHqDomain hhq(StockDomain stockDomain, String date) throws ExecutionException, InterruptedException {
        // call rest service
        return CompletableFuture.supplyAsync(() -> restTemplate.getHhqData(stockDomain, date)).get();
    }
}
