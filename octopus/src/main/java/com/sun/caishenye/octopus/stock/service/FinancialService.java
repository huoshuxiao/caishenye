package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.agent.webmagic.FinancialReportEastMoneyYJBBPageProcessor;
import com.sun.caishenye.octopus.stock.agent.webmagic.FinancialReportStep1PageProcessor;
import com.sun.caishenye.octopus.stock.agent.webmagic.FinancialReportStep2PageProcessor;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.FinancialReport2Domain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 财报
 */
@Slf4j
@Service
public class FinancialService {

    // 财务报表:财务摘要 —新浪财经
    // https://money.finance.sina.com.cn/corp/go.php/vFD_FinanceSummary/stockid/600647.phtml
    private static final String FR_BASE_URL = "https://money.finance.sina.com.cn/corp/go.php/vFD_FinanceSummary/stockid/{companyCode}.phtml";

    // 财务报表:财务指标 —新浪财经
    // https://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/600647/ctrl/2019/displaytype/4.phtml
    private static final String FR_GUIDE_LINE_URL = "https://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/{companyCode}/ctrl/{year}/displaytype/4.phtml";

    // 东方财富网 - 数据中心 - 业绩大全 - 业绩报表
    // http://data.eastmoney.com/bbsj/yjbb/600732.html
    private static final String FR_EASTMONEY_YJBB_URL = "http://data.eastmoney.com/bbsj/yjbb/{companyCode}.html";

    @Autowired
    private FinancialReportStep1PageProcessor frPageProcessor;

    @Autowired
    private FinancialReportStep2PageProcessor frglPageProcessor;

    @Autowired
    private FinancialReportEastMoneyYJBBPageProcessor eastMoneyYJBBPageProcessor;

    @Autowired
    private StockDao stockDao;

    @Autowired
    private BaseService baseService;

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    // 财务报表 (东方财富网)
    public Object financialReport2() throws ExecutionException, InterruptedException {
        String tag = "financialReport2";
        LocalDateTime startTime = LocalDateTime.now();

        // 查询证券基础数据
        List<StockDomain> stockDomainList = baseService.readBaseData();

        // 采集 财务数据(业绩报表) 东方财富网
        List<FinancialReport2Domain> frYjbbList = Collections.synchronizedList(new ArrayList<>());
        for (StockDomain stockDomain: stockDomainList) {
            frYjbbList.addAll(agentYjbbData(stockDomain));
        }

        // 财报
        stockDao.writeFinancialReport2(frYjbbList);
//
//        // 业绩报表
//        List<String> urls = new ArrayList<>(stockDomainList.size());
//        for (StockDomain stockDomain : stockDomainList) {
//            urls.add(FR_EASTMONEY_YJBB_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
//        }
//        // 采集数据
//        eastMoneyYJBBPageProcessor.run(urls);
//
//        // 财报
//        stockDao.writeFinancialReport2(stockDao.readFinancialReport2());

        LocalDateTime endTime = LocalDateTime.now();
        return tag + " " + ChronoUnit.MINUTES.between(startTime, endTime);
    }

    // 采集 财务数据(业绩报表) 东方财富网
    private List<FinancialReport2Domain> agentYjbbData(StockDomain stockDomain) throws ExecutionException, InterruptedException {
        // call rest service
        CompletableFuture<List<FinancialReport2Domain>> future = CompletableFuture.supplyAsync(() -> apiRestTemplate.getFrYjbbForObject(stockDomain)).get();
        return future.get();
    }

    // 财务报表 (新浪财经)
    public Object financialReport() {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = baseService.readBaseData();

        // 新浪财经 财务摘要
        List<String> urls = new ArrayList<>(stockDomainList.size());
        for (StockDomain stockDomain : stockDomainList) {
            urls.add(FR_BASE_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
        }

        // 采集 新浪财经 财务摘要
        frPageProcessor.run(urls);

        // 读 新浪财经 财务摘要
        urls.clear();
        stockDomainList.clear();
        stockDomainList = stockDao.readFinancialReportStep1();  // 186151

        // ============================================================================================

        // 新浪财经 财务指标
        Map<String, String> codeAndYearFilterMap = new HashMap<>();
        Map<String, StockDomain> stockDomainMap = new HashMap<>();
        stockDomainList.stream().forEach(stockDomain -> {
//        for (StockDomain stockDomain : stockDomainList) {
            String code = stockDomain.getCompanyCode();
            String year = stockDomain.getFrDomain().getDeadline().substring(0, 4);
            stockDomainMap.put(code + stockDomain.getFrDomain().getDeadline(), stockDomain);
            // 财年数据为采集
            if (StringUtils.isEmpty(codeAndYearFilterMap.get(code + year))) {
                codeAndYearFilterMap.put(code + year, code + year);
                urls.add(FR_GUIDE_LINE_URL.replace("{companyCode}", code).replace("{year}", year));
            }
//        }
        });

        // 新浪财经 财务指标
        frglPageProcessor.run(urls);

        // merge fr (step1 -> step2)
        List<StockDomain> stockDomainList2 = stockDao.readFinancialReportStep2();   // 185749
        stockDomainList2.stream().forEach(t -> {
            String key = t.getCompanyCode() + t.getFrDomain().getDeadline();
            StockDomain stockDomain = stockDomainMap.get(key);

            if (stockDomain == null) {

                t.getFrDomain().setMainBusinessIncome("*");
                t.getFrDomain().setNetProfit("*");
                t.getFrDomain().setNetMargin("*");
                log.error("fr not found :: {}", key);

            } else {

                t.getFrDomain().setMainBusinessIncome(stockDomain.getFrDomain().getMainBusinessIncome());
                t.getFrDomain().setNetProfit(stockDomain.getFrDomain().getNetProfit());
                t.getFrDomain().setNetMargin(stockDomain.getFrDomain().getNetMargin());
            }
        });

        // 财报
        stockDao.writeFinancialReport(stockDomainList2);

        return "finished";
    }
}
