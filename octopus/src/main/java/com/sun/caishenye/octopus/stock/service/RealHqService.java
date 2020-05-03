package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 实时/每日 行情
 */
@Slf4j
@Service
public class RealHqService {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private BaseService baseService;

    @Autowired
    private ShService shService;

    @Autowired
    private SzService szService;

    // 实时行情
    public Object hq() throws ExecutionException, InterruptedException {
        // 查询证券基础数据
        List<StockDomain> stockDomainList = baseService.readBaseData();
        for (StockDomain stockDomain: stockDomainList) {
            if (Constants.EXCHANGE_SZ.getString().equals(stockDomain.getExchange())) {
                szService.hq(stockDomain);
            } else {
                shService.hq(stockDomain);
            }
        }

        // 写入 实时行情 数据
        writeHqData(stockDomainList);

        return "hq";
    }

    // 写 实时行情
    public void writeHqData(List<StockDomain> stockDomainList) {
        stockDao.writeHqData(stockDomainList);
    }

    public List<StockDomain> readHqData() {
        return stockDao.readHqData();
    }
}
