package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.MoneyFlowDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 资金流
 */
@Slf4j
@Service
public class MoneyFlowService {

    @Autowired
    private BaseService baseService;

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    @Autowired
    private StockDao stockDao;

    @Value("${mf.exchange}")
    private String exchange;
    @Value("${mf.code:0}")
    private int companyCode;
    @Value("${sleep.run:false}")
    private boolean sleepFlg;

    // 个股
//    @Async
    public void stock() throws ExecutionException, InterruptedException {
        // 查询证券基础数据
        List<StockDomain> _baseList = baseService.readBaseData();

        List<StockDomain>  szBaseList = _baseList.stream()
                                                .filter(t -> t.getExchange().equalsIgnoreCase(Constants.EXCHANGE_SZ.getString()))
                                                .collect(Collectors.toList());

        List<StockDomain>  shBaseList = _baseList.stream()
                                                .filter(t -> t.getExchange().equalsIgnoreCase(Constants.EXCHANGE_SH.getString()))
                                                .collect(Collectors.toList());
        List<StockDomain>  baseList2 = _baseList.stream()
                                                    .filter(t -> new Integer(t.getCompanyCode()) < companyCode)
                                                    .collect(Collectors.toList());

        List<StockDomain> baseList = new ArrayList<>();
        if (companyCode == 999999) {
            baseList.addAll(szBaseList);
            baseList.addAll(shBaseList);
        } else {
            if (exchange.equalsIgnoreCase(Constants.EXCHANGE_SZ.getString())) {
                baseList.addAll(szBaseList);
            }
            if (exchange.equalsIgnoreCase(Constants.EXCHANGE_SH.getString())) {
                baseList.addAll(shBaseList);
            }
            if (companyCode != 0) {
                baseList.addAll(baseList2);
            }
        }

        List<MoneyFlowDomain> resultList = new ArrayList<>();
        for (StockDomain stockDomain: baseList) {
            // 采集 个股资金流
            List<String> klines = agentStockData(stockDomain).getKlines();
            // 构建 历史行情 实体 写入用
            for (String data: klines) {
                MoneyFlowDomain domain = new MoneyFlowDomain();
                // 公司代码
                domain.setCompanyCode(stockDomain.getCompanyCode());
                // 公司简称
                domain.setCompanyName(stockDomain.getCompanyName());
                domain.getKlines().add(data);

                resultList.add(domain);
            }
        }
        // 写入 历史行情 数据
        writeStockData(resultList);
    }

    // 采集 个股资金流
    private MoneyFlowDomain agentStockData(StockDomain stockDomain) throws ExecutionException, InterruptedException {

        if (sleepFlg) {
            return apiRestTemplate.getStockMoneyFlow(stockDomain);
        }
        // call rest service
        CompletableFuture<MoneyFlowDomain> data = CompletableFuture.supplyAsync(() -> apiRestTemplate.getStockMoneyFlowAsync(stockDomain)).join();
        return data.get();
    }

    // 写 个股资金流
    public void writeStockData(List<MoneyFlowDomain> list) {
        stockDao.writeStockData(list);
    }
}
