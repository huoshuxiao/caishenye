package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.MoneyFlowDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import com.sun.caishenye.octopus.stock.domain.TenHolderDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 股本股东
 */
@Slf4j
@Service
public class ShareHolderService {

    @Autowired
    private BaseService baseService;

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    @Autowired
    private StockDao stockDao;

    // 十大股东
    public void tenHolder() throws ExecutionException, InterruptedException {

        List<TenHolderDomain> resultList = new ArrayList<>();
        // 查询证券基础数据
        List<StockDomain> baseList = baseService.readBaseData();
        for (StockDomain baseDomain: baseList) {
            if (baseDomain.getCompanyName().contains("退")) {
                continue;
            }
            TenHolderDomain domain = agentTenHolderData(baseDomain);
            // 未上市
            if (domain == null) {
                continue;
            }
            // 构建 历史行情 实体 写入用
            resultList.add(domain);
        }
        // 写入 历史行情 数据
        writeTenHolderData(resultList);
    }

    // 采集 十大股东
    private TenHolderDomain agentTenHolderData(StockDomain stockDomain) throws ExecutionException, InterruptedException {
        // call rest service
        CompletableFuture<TenHolderDomain> data = CompletableFuture.supplyAsync(() -> apiRestTemplate.getTenHolder(stockDomain)).get();
        return data.get();
    }

    // 写 十大股东
    public void writeTenHolderData(List<TenHolderDomain> list) {
        stockDao.writeTenHolderData(list);
    }
}
