package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
        int _count = apiRestTemplate.getBaseCount();

        int count = (int)Math.ceil((double) _count / 100);
//        List<CompletableFuture<List<StockDomain>>> futures = new ArrayList<>(_count);
        List<List<StockDomain>> futures = new ArrayList<>(_count);
        for (int i = 1; i <= count; i++) {
//            int finalI = i;
//            CompletableFuture<List<StockDomain>> future = CompletableFuture.supplyAsync(() -> apiRestTemplate.getBaseForObject(finalI)).join();
            List<StockDomain> future = apiRestTemplate.getBaseForObject(i);
            futures.add(future);
        }

        List<StockDomain> data = new ArrayList<>(_count);
//        // 等待所有任务完成，并获取结果
//        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        // 获取结果列表
//        allDone.thenRun(() -> {
//            List<List<StockDomain>> results = futures.stream()
//                    .map(future -> {
//                        try {
//                            return future.get(); // get() 会阻塞直到有结果
//                        } catch (InterruptedException | ExecutionException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }).collect(Collectors.toList());
//            results.forEach(data::addAll);
//        }).get(); // 阻塞等待所有任务完成
        List<List<StockDomain>> results = new ArrayList<>(futures);
        results.forEach(data::addAll);

        stockDao.writeBaseData(data);
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
