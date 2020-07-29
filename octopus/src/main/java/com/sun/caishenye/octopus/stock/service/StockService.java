package com.sun.caishenye.octopus.stock.service;


import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.ShareBonusDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 证券 总服务
 */
@Slf4j
@Service
public class StockService {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private RealHqService realHqService;

    @Autowired
    private ShareBonusService shareBonusService;

    @Autowired
    private FinancialService financialService;

    @Autowired
    private HistoryHqService historyHqService;

    public void run() throws ExecutionException, InterruptedException {
        hq();
        shareBonus();
//        hhq();
        financialReport();

        moneyMoney();
    }

    // 财务报表
    public Object financialReport() throws ExecutionException, InterruptedException {
//        financialService.financialReport();
        return financialService.financialReport2();
    }

    // 分红配股
    public Object shareBonus() {
        return shareBonusService.shareBonus();
    }

    // 历史行情
    public Object hhq() throws ExecutionException, InterruptedException {

        return historyHqService.hhq();
    }

    // 实时行情
    public Object hq() throws ExecutionException, InterruptedException {
        return realHqService.hq();
    }

    // =================== 计算 =========================================
    // 钱多多
    // 扩展 分红配股
    public Object moneyMoney() {

        // 分红配股
        List<StockDomain> shareBonusDataList = shareBonusService.readShareBonus();

        // 实时行情
        List<StockDomain> hqDataList = realHqService.readHqData();
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
//        Map<String, DayLineDomain> hhqDataMap = hhqDataList.stream().collect(Collectors.toMap(t -> t.getCompanyCode() + t.getDay(), t-> t));

        // merge指标，计算股息率(扩展分红配股 股价/股息率)
        shareBonusDataList.stream().forEach(stockDomain -> {

            log.debug("mm stockDomain value>> {}", stockDomain);

            // 预案
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
            // 实施
            // 历史 股息率,用 历史行情 数据 计算
            } else if (sbDomain.getSchedule().equals(Constants.SB_SCHEDULE_IMPLEMENT.getString())
                    && Double.valueOf(sbDomain.getDividend()).doubleValue() > 0d) { // 分红金额大于0

                // 根据 公司代码 + 除权除息日(map) 取得 股价
                String mapKey = stockDomain.getCompanyCode() + Utils.formatDate2String(sbDomain.getDividendDate());
                log.debug("mm mapKey :: {}", mapKey);
//                DayLineDomain dayLineDomain = hhqDataMap.get(mapKey);
                DayLineDomain dayLineDomain = historyHqService.getHhqByDateForObject(stockDomain);
                // 历史数据 没有时 不计算股息率
                if (dayLineDomain == null) {
                    log.error("mm mapKey :: {}", mapKey);
                    if ("--".equals(sbDomain.getDividendDate())) {
                        stockDomain.setDividendYield("×");
                        stockDomain.setPrice("×");
                        stockDomain.setDate(sbDomain.getDividendDate());
                    } else {
                        // 除权除息日 大于等于 当日（数据问题） 取实时行情
                        LocalDate today = LocalDate.now();
                        LocalDate dividendLocalDate = LocalDate.of(Integer.valueOf(sbDomain.getDividendDate().substring(0, 4)).intValue(),
                                Integer.valueOf(sbDomain.getDividendDate().substring(5, 7)).intValue(),
                                Integer.valueOf(sbDomain.getDividendDate().substring(8, 10)).intValue());
                        if (ChronoUnit.DAYS.between(dividendLocalDate, today) <= 0) {
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
                        } else {
                            stockDomain.setDividendYield("×");
                            stockDomain.setPrice("×");
                            stockDomain.setDate(sbDomain.getDividendDate());
                        }
                    }
                } else {
                    stockDomain.setPrice(dayLineDomain.getPrice());
                    stockDomain.setDate(sbDomain.getDividendDate());
                    // 股息率 = 派息(税前)(元) / 股价
                    stockDomain.setDividendYield(calDividendYield(stockDomain));
                }
            // 不分配 股息率=0
            } else if (sbDomain.getSchedule().equals(Constants.SB_SCHEDULE_NOT_ASSIGNED.getString())) {
//                // 不分配 无除权除息日,取公告日作为除权除息日
//                // 根据 公司代码 + 公告日(map) 取得 股价
//                String mapKey = stockDomain.getCompanyCode() + Utils.formatDate2String(sbDomain.getBonusDate());
//                sbDomain.setDividendDate(Utils.formatDate2String(sbDomain.getBonusDate()));
////                DayLineDomain dayLineDomain = hhqDataMap.get(mapKey);
//                DayLineDomain dayLineDomain = historyHqService.getHhqByDateForObject(stockDomain);
//                // 历史数据 没有时 不计算股息率
//                if (dayLineDomain == null) {
//                    log.error("mm mapKey :: {}", mapKey);
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

    private void writeMoneyMoney(List<StockDomain> data) {
        stockDao.writeMoneyMoney(data);
    }
}
