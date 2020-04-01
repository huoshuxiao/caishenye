package com.sun.caishenye.octopus.stock.service;


import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.business.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.business.webmagic.FinancialReportStep1DataPageProcessor;
import com.sun.caishenye.octopus.stock.business.webmagic.FinancialReportStep2DataPageProcessor;
import com.sun.caishenye.octopus.stock.business.webmagic.ShareBonusDataPageProcessor;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.ShareBonusDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
    // https://money.finance.sina.com.cn/corp/go.php/vFD_FinanceSummary/stockid/600647.phtml
    protected final String FR_BASE_URL = "https://money.finance.sina.com.cn/corp/go.php/vFD_FinanceSummary/stockid/{companyCode}.phtml";

    // 财务报表:财务指标 —新浪财经
    // https://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/600647/ctrl/2019/displaytype/4.phtml
    protected final String FR_GUIDE_LINE_URL = "https://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/{companyCode}/ctrl/{year}/displaytype/4.phtml";

    // 发行与分配:分红配股 — 新浪财经
    protected final String SB_BASE_URL = "https://money.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/{companyCode}.phtml";

    @Autowired
    private ShService shService;

    @Autowired
    private SzService szService;

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    @Autowired
    private FinancialReportStep1DataPageProcessor frPageProcessor;
    @Autowired
    private FinancialReportStep2DataPageProcessor frglPageProcessor;

    @Autowired
    private ShareBonusDataPageProcessor sbPageProcessor;

    @Autowired
    private StockDao stockDao;

    public void run() throws ExecutionException, InterruptedException {
        hq();

        shareBonus();
//        hhq();
        moneyMoney();

        financialReport();
    }

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

    public List<StockDomain> readShareBonus() {
        return stockDao.readShareBonus();
    }

    // 财务报表
    public Object financialReport() {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = readBaseData();

        List<String> urls = new ArrayList<>(stockDomainList.size());
        for (StockDomain stockDomain : stockDomainList) {
            urls.add(FR_BASE_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
        }

        // 新浪财经 财务摘要
        frPageProcessor.run(urls);

        // 读 新浪财经 财务摘要
        urls.clear();
        stockDomainList.clear();
        stockDomainList = stockDao.readFinancialReportStep1();  // 186151

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
        stockDomainList2.parallelStream().forEach(t -> {
            String key = t.getCompanyCode() + t.getFrDomain().getDeadline();
            StockDomain stockDomain = stockDomainMap.get(key);

            if (stockDomain == null) {

                t.getFrDomain().setMainBusinessIncome("*");
                t.getFrDomain().setNetProfit("*");
                t.getFrDomain().setNetMargin("*");
                log.warn("fr not found :: {}", key);

            } else {

                t.getFrDomain().setMainBusinessIncome(stockDomain.getFrDomain().getMainBusinessIncome());
                t.getFrDomain().setNetProfit(stockDomain.getFrDomain().getNetProfit());
                t.getFrDomain().setNetMargin(stockDomain.getFrDomain().getNetMargin());
            }
        });

        stockDao.writeFinancialReport(stockDomainList2);

        return "finished";
    }

    // 历史行情
    public Object hhq() throws ExecutionException, InterruptedException {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = readBaseData();
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
    public DayLineDomain agentHhqData(StockDomain stockDomain) throws ExecutionException, InterruptedException {
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

    // 写 实时行情
    public void writeHqData(List<StockDomain> stockDomainList) {
        stockDao.writeHqData(stockDomainList);
    }

    private List<StockDomain> readHqData() {
        return stockDao.readHqData();
    }

    // 查询证券基础数据
    public List<StockDomain> readBaseData() {
        List<StockDomain> shStockDomainList = shService.readBaseData();
        List<StockDomain> szStockDomainList = szService.readBaseData();
        return Stream.concat(shStockDomainList.stream(), szStockDomainList.stream()).collect(Collectors.toList());
    }

    // 钱多多
    // 扩展 分红配股
    public Object moneyMoney() {

        // 分红配股
        List<StockDomain> shareBonusDataList = readShareBonus();

        // 实时行情
        List<StockDomain> hqDataList = readHqData();
        Map<String, StockDomain> hqDataMap = new HashMap<>();
        for (StockDomain stockDomain: hqDataList) {
            hqDataMap.put(stockDomain.getCompanyCode(), stockDomain);
        }

        // 历史行情
//        List<DayLineDomain> hhqDataList = readHhqData();
//        int hhqCount = hhqDataList.size();
//        log.info("hhq data size :: {} sb data size :: {}", hhqCount, shareBonusDataList.size());
//
//        // 空间（map）换时间(list)
//        Map<String, DayLineDomain> hhqDataMap = hhqDataList.parallelStream().collect(Collectors.toMap(t -> t.getCompanyCode() + t.getDay(), t-> t));

        // merge指标，计算股息率(扩展分红配股 股价/股息率)
        shareBonusDataList.parallelStream().forEach(stockDomain -> {

            log.debug("mm stockDomain value>> {}", stockDomain);

            // 当年 股息率 预测,用 实时行情 数据 计算
            ShareBonusDomain sbDomain = stockDomain.getSbDomain();
            log.debug("mm sbDomain value>> {}", sbDomain);
            if (sbDomain.getSchedule().equals(Constants.SB_SCHEDULE_PLAN.getString())) {

                // 实时行情 股价
                String price = hqDataMap.get(stockDomain.getCompanyCode()).getPrice();
                stockDomain.setPrice(price);
                stockDomain.setDate(hqDataMap.get(stockDomain.getCompanyCode()).getDate());

                // 停牌时 不计算股息率
                if (Constants.HQ_SUSPENSION.getString().equals(price)) {
                    stockDomain.setDividendYield("-");
                } else {
                    // 股息率 = 派息(税前)(元) / 股价
                    stockDomain.setDividendYield(calDividendYield(stockDomain));
                }
            // 历史 股息率,用 历史行情 数据 计算
            } else if (sbDomain.getSchedule().equals(Constants.SB_SCHEDULE_IMPLEMENT.getString())
                    && Double.valueOf(sbDomain.getDividend()).doubleValue() > 0d) { // 分红金额大于0
                // 根据 公司代码 + 除权除息日(map) 取得 股价
                String mapKey = stockDomain.getCompanyCode() + Utils.formatDate2String(sbDomain.getDividendDate());
//                DayLineDomain dayLineDomain = hhqDataMap.get(mapKey);
                DayLineDomain dayLineDomain = apiRestTemplate.getHhqByDateForObject(stockDomain);
                // 历史数据 没有时 不计算股息率
                if (dayLineDomain == null) {
                    log.warn("mm mapKey :: {}", mapKey);
                    stockDomain.setDividendYield("×");
                    stockDomain.setPrice("×");
                } else {
                    stockDomain.setPrice(dayLineDomain.getPrice());
                    // 股息率 = 派息(税前)(元) / 股价
                    stockDomain.setDividendYield(calDividendYield(stockDomain));
                }
                stockDomain.setDate(sbDomain.getDividendDate());
            // 不分配 股息率=0
            } else if (sbDomain.getSchedule().equals(Constants.SB_SCHEDULE_NOT_ASSIGNED.getString())) {
//                // 不分配 无除权除息日,取公告日作为除权除息日
//                // 根据 公司代码 + 公告日(map) 取得 股价
//                String mapKey = stockDomain.getCompanyCode() + Utils.formatDate2String(sbDomain.getBonusDate());
//                sbDomain.setDividendDate(Utils.formatDate2String(sbDomain.getBonusDate()));
////                DayLineDomain dayLineDomain = hhqDataMap.get(mapKey);
//                DayLineDomain dayLineDomain = apiRestTemplate.getHhqByDateForObject(stockDomain);
//                // 历史数据 没有时 不计算股息率
//                if (dayLineDomain == null) {
//                    log.warn("mm mapKey :: {}", mapKey);
//                    stockDomain.setPrice("-");
//                } else {
//                    stockDomain.setPrice(dayLineDomain.getPrice());
//                }
                stockDomain.setPrice("-");
                stockDomain.setDividendYield("0");
                stockDomain.setDate(sbDomain.getDividendDate());
            // 非分红(送股(股)/转增(股))
            } else {
                stockDomain.setPrice("-");
                stockDomain.setDividendYield("0");
                stockDomain.setDate(sbDomain.getDividendDate());
            }
        });

        writeMoneyMoney(shareBonusDataList);

        return "mm";
    }

    // 计算股息率
    private String calDividendYield(StockDomain stockDomain) {
        log.debug("calDividendYield {} ::", stockDomain.toString());
        // 股息率 = 派息(税前)(元) / 股价
        String price = String.valueOf(Double.valueOf(stockDomain.getPrice()) * 10);
        return Utils.rate(stockDomain.getSbDomain().getDividend(), price);
    }

    public void writeMoneyMoney(List<StockDomain> data) {
        stockDao.writeMoneyMoney(data);
    }
}
