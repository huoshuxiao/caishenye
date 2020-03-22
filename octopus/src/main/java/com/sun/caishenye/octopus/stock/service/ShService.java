package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.business.api.ShRestTemplate;
import com.sun.caishenye.octopus.stock.business.webmagic.ShBaseDataPageProcessor;
import com.sun.caishenye.octopus.stock.dao.ShDao;
import com.sun.caishenye.octopus.stock.domain.PageHelpDomain;
import com.sun.caishenye.octopus.stock.domain.SseQueryDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private ShBaseDataPageProcessor shangZhengBaseDataPageProcessor;

    @Autowired
    private ShRestTemplate template;

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


    public List<StockDomain> readBaseData() {

        return shangZhengDao.readBaseData();
    }
}
