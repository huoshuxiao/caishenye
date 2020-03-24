package com.sun.caishenye.octopus.stock.service;


import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.stock.business.webmagic.FinancialReportDataPageProcessor;
import com.sun.caishenye.octopus.stock.business.webmagic.ShareBonusDataPageProcessor;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 证券 总服务
 */
@Slf4j
@Service
public class StockService {

    // 财务报表:财务摘要 —新浪财经
    protected final String FR_BASE_URL = "https://money.finance.sina.com.cn/corp/go.php/vFD_FinanceSummary/stockid/{companyCode}.phtml";

    // 发行与分配:分红配股 — 新浪财经
    protected final String SB_BASE_URL = "https://money.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/{companyCode}.phtml";

    @Autowired
    private ShService shService;

    @Autowired
    private SzService szService;

    @Autowired
    private FinancialReportDataPageProcessor frPageProcessor;

    @Autowired
    private ShareBonusDataPageProcessor sbPageProcessor;

    @Autowired
    private StockDao stockDao;

    // 分红配股
    public Object shareBonus() {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = readBaseData();

        List<String> urls = new ArrayList<>(stockDomainList.size());
        for (StockDomain stockDomain: stockDomainList) {
            urls.add(SB_BASE_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
        }

        // 新浪财经 分红配股
        sbPageProcessor.run(urls);
        return "finished";
    }

    // 财务报表
    public Object financialReport() {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = readBaseData();

        List<String> urls = new ArrayList<>(stockDomainList.size());
        for (StockDomain stockDomain: stockDomainList) {
            urls.add(FR_BASE_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
        }

        // 新浪财经 财务报表
        frPageProcessor.run(urls);

        return "finished";
    }

    // 实时行情
    public Object hq() throws ExecutionException, InterruptedException {
        // 查询证券基础数据
        List<StockDomain> stockDomainList = readBaseData();
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

    public void writeHqData(List<StockDomain> stockDomainList) {
        stockDao.writeHqData(stockDomainList);
    }

    // 查询证券基础数据
    public List<StockDomain> readBaseData() {
        List<StockDomain> shStockDomainList = shService.readBaseData();
        List<StockDomain> szStockDomainList = szService.readBaseData();
        return Stream.concat(shStockDomainList.stream(), szStockDomainList.stream()).collect(Collectors.toList());
    }
}
