package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.FinancialReport2Domain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基础数据
 */
@Slf4j
@Service
public class BaseService {

    @Autowired
    private ShService shService;

    @Autowired
    private SzService szService;

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    @Autowired
    private StockDao stockDao;

    // 基础数据
    public String base() throws ExecutionException, InterruptedException {
        // call rest service
        CompletableFuture<List<StockDomain>> future = CompletableFuture.supplyAsync(() -> apiRestTemplate.getBaseForObject()).get();
        stockDao.writeBaseData(future.get());
        return "finished";
    }

    // 查询证券基础数据
    public List<StockDomain> readBaseData() {
//        List<StockDomain> shStockDomainList = shService.readBaseData();
//        List<StockDomain> szStockDomainList = szService.readBaseData();
//        return Stream.concat(shStockDomainList.stream(), szStockDomainList.stream()).collect(Collectors.toList());

        return stockDao.readBaseData();
    }
}
