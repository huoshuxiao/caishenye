package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.stock.agent.api.ShRestTemplate;
import com.sun.caishenye.octopus.stock.dao.ShDao;
import com.sun.caishenye.octopus.stock.domain.ShHqDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 上海证券
 */
@Slf4j
@Service
public class ShService {

    @Autowired
    private ShDao shangZhengDao;

    @Autowired
    private ShRestTemplate restTemplate;

/*
    @Autowired
    private ShBaseDataPageProcessor shangZhengBaseDataPageProcessor;

    public Object base2() {
        shangZhengBaseDataPageProcessor.run();
        return "finished";
    }

    public Object base() throws ExecutionException, InterruptedException {

        // 第一次提取，获取提取范围
        PageHelpDomain pageHelpDomain = new PageHelpDomain();
        pageHelpDomain.setCacheSize(1);
        pageHelpDomain.setBeginPage(1);
        pageHelpDomain.setPageSize(25);
        pageHelpDomain.setPageNo(1);
        pageHelpDomain.setEndPage(11);
        SseQueryDomain queryDomain = template.getBaseForObject(pageHelpDomain).get();
        List<StockDomain> writeDataList = new ArrayList<>(queryDomain.getPageHelpDomain().getTotal());

        List<CompletableFuture<SseQueryDomain>> futureList = new ArrayList<>();
        pageHelpDomain = queryDomain.getPageHelpDomain();
        for (int i = 1; i <= pageHelpDomain.getPageCount(); i++) {
            PageHelpDomain newPageHelpDomain = null;
            BeanUtils.copyProperties(pageHelpDomain, newPageHelpDomain);
            newPageHelpDomain.setBeginPage(newPageHelpDomain.getBeginPage() + 1);
            newPageHelpDomain.setPageNo(newPageHelpDomain.getPageNo() + 1);
            newPageHelpDomain.setEndPage(newPageHelpDomain.getEndPage() + 10);
            // call rest service
            CompletableFuture<SseQueryDomain> future = CompletableFuture.supplyAsync(() -> template.getBaseForObject(newPageHelpDomain)).get();
            futureList.add(future);
        }

        for (CompletableFuture<SseQueryDomain> future: futureList) {
            for (StockDomain stockDomain: future.get().getPageHelpDomain().getStockDomain()) {
                writeDataList.add(stockDomain);
            }
        }

        writeData(writeDataList);
        return "finished";
    }

    public void writeData(List<StockDomain> writeDataList) {
        shangZhengDao.writeData(writeDataList);
    }
    */


    public List<StockDomain> readBaseData() {

        return shangZhengDao.readBaseData();
    }

    // 实时行情 price
    public void hq(StockDomain stockDomain) throws ExecutionException, InterruptedException {

        // call rest service
        CompletableFuture<ShHqDomain> hqDomainCompletableFuture = CompletableFuture.supplyAsync(() -> restTemplate.getHqForObject(stockDomain)).get();
        ShHqDomain shHqDomain = hqDomainCompletableFuture.get();
        // 停牌
        if (shHqDomain.getLine().size() == 0) {
            shHqDomain.setPrice(Constants.HQ_SUSPENSION.getString());
        } else {
            Number[] numbers = shHqDomain.getLine().get(shHqDomain.getLine().size() - 1);
            shHqDomain.setPrice(numbers[1].toString());
        }
        stockDomain.setPrice(shHqDomain.getPrice());
    }

//    // 历史行情
//    public void hhq(StockDomain stockDomain) throws ExecutionException, InterruptedException {
//        // call rest service
//        CompletableFuture<ShHqDomain> hqDomainCompletableFuture = CompletableFuture.supplyAsync(() -> restTemplate.getHhqForObject(stockDomain)).get();
//        ShHqDomain shHqDomain = hqDomainCompletableFuture.get();
//    }
}
