package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 历史行情
 */
@Slf4j
@Service
public class HistoryHqService {

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    @Autowired
    private StockDao stockDao;

    @Autowired
    private BaseService baseService;

    public DayLineDomain getHhqByDateForObject(StockDomain stockDomain) {
        return apiRestTemplate.getHhqByDateForObject(stockDomain);
    }

    // 历史行情
    public Object hhq() throws ExecutionException, InterruptedException {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = baseService.readBaseData();
        List<DayLineDomain> hhqList = new ArrayList<>();
        for (StockDomain stockDomain: stockDomainList) {
            // 采集 历史行情
            List<String[]> hqsList = agentHhqData(stockDomain).getHqs();
            // 构建 历史行情 实体 写入用
            for (String[] data: hqsList) {
                DayLineDomain dayLineDomain = new DayLineDomain();
                // 公司代码
                dayLineDomain.setCompanyCode(stockDomain.getCompanyCode());
                // 公司简称
                dayLineDomain.setCompanyName(stockDomain.getCompanyName());
                // 收盘日
                dayLineDomain.setDay(data[0]);
                // 收盘价
                dayLineDomain.setPrice(data[2]);

                hhqList.add(dayLineDomain);
            }
        }
        // 写入 历史行情 数据
        writeHhqData(hhqList);

        return "hhq";
    }

    // 采集 历史行情
    private DayLineDomain agentHhqData(StockDomain stockDomain) throws ExecutionException, InterruptedException {
        // call rest service
        CompletableFuture<DayLineDomain> hqDomainCompletableFuture = CompletableFuture.supplyAsync(() -> apiRestTemplate.getHhqForObject(stockDomain)).get();
        return hqDomainCompletableFuture.get();
    }

    // 写 历史行情
    public void writeHhqData(List<DayLineDomain> hhqList) {
        stockDao.writeHhqData(hhqList);
    }
    // 读 历史行情
    public List<DayLineDomain> readHhqData() {
        return stockDao.readHhqData();
    }
}
