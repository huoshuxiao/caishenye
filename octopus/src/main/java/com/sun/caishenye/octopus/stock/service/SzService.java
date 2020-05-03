package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.stock.agent.api.SzRestTemplate;
import com.sun.caishenye.octopus.stock.dao.SzDao;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.SzHqDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        CompletableFuture<SzHqDomain> hqDomainCompletableFuture = CompletableFuture.supplyAsync(() -> restTemplate.getHqForObject(stockDomain)).get();
        SzHqDomain hqDomain = hqDomainCompletableFuture.get();
        hqDomain.setPrice(hqDomain.getData().getNow() == null ? Constants.HQ_SUSPENSION.getString() : hqDomain.getData().getNow());
        stockDomain.setPrice(hqDomain.getPrice());
    }

//    // 历史行情
//    public void hhq(StockDomain stockDomain) throws ExecutionException, InterruptedException {
//        // call rest service
//        CompletableFuture<SzHqDomain> hqDomainCompletableFuture = CompletableFuture.supplyAsync(() -> restTemplate.getHhqForObject(stockDomain)).get();
//        SzHqDomain hqDomain = hqDomainCompletableFuture.get();
//    }
}
