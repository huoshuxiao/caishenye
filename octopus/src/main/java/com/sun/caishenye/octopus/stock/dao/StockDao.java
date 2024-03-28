package com.sun.caishenye.octopus.stock.dao;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.component.CacheComponent;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.FinancialReport2Domain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class StockDao {

    @Autowired
    private CacheComponent cache;

    private String getFilePath() {
        return cache.putIfAbsentFilePath();
    }

    // 写 基础数据
    public void writeBaseData(List<StockDomain> data) {
        log.info("基础数据::{}",data.size());
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_STOCK_BASE.getString()), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.baseBuilder() + "\r\n";
                log.debug("write base data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    // 读 基础数据
    public List<StockDomain> readBaseData() {

        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(getFilePath() + Constants.FILE_STOCK_BASE.getString());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read base data >> {}", line);

                StockDomain stockDomain = new StockDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());
                // 公司代码
                stockDomain.setCompanyCode(extendDomainArray[0].trim());
                // 公司简称
                stockDomain.setCompanyName(extendDomainArray[1].trim());
                // 证券交易所
                stockDomain.setExchange(extendDomainArray[2].trim());

                list.add(stockDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }

    public void writeMoneyMoney(List<StockDomain> data) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_MONEY_MONEY.getString()), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.mmBuilder() + "\r\n";
                log.debug("write mm data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    // 写 实时行情
    public void writeHqData(List<StockDomain> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_HQ.getString()), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.hqBuilder() + "\r\n";
                log.debug("write hq data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    // 读 实时行情
    public List<StockDomain> readHqData() {
        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(getFilePath() + Constants.FILE_HQ.getString());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read hq data >> {}", line);

                StockDomain stockDomain = new StockDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());

                // 公司代码
                stockDomain.setCompanyCode(extendDomainArray[0].trim());
                // 公司简称
                stockDomain.setCompanyName(extendDomainArray[1].trim());
                // 交易日
                stockDomain.setDate(extendDomainArray[2].trim());
                // 股价
                stockDomain.setPrice(extendDomainArray[3].trim());

                list.add(stockDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }
//
//    // TODO 业绩修正导致的数据不一致
//    // 写 财报
//    public void writeFinancialReport(List<StockDomain> data) {
//
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FR_DATA_FILE), StandardCharsets.UTF_8)) {
//            for (StockDomain stockDomain : data) {
//                String s = stockDomain.frBuilder() + "\r\n";
//                log.debug("write fr data >> {}", s);
//                writer.write(s, 0, s.length());
//            }
//        } catch (IOException x) {
//            log.error(String.format("IOException: %s%n", x));
//        }
//    }
//
//    // 读 财务摘要
//    public List<StockDomain> readFinancialReportStep1() {
//
//        List<StockDomain> list = new ArrayList<>();
//        Path path = Paths.get(FR_STEP1_DATA_FILE);
//
//        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                log.debug("read fr step1 data >> {}", line);
//
//                StockDomain stockDomain = new StockDomain();
//                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());
//
//                // 公司代码
//                stockDomain.setCompanyCode(extendDomainArray[0].trim());
//                // 公司简称
//                stockDomain.setCompanyName(extendDomainArray[1].trim());
//                // 截止日期
//                stockDomain.getFrDomain().setDeadline(extendDomainArray[2].trim());
//                // 主营业务收入(亿元)
//                stockDomain.getFrDomain().setMainBusinessIncome(extendDomainArray[3].trim());
//                // 净利润(亿元)
//                stockDomain.getFrDomain().setNetProfit(extendDomainArray[4].trim());
//                // 净利润率(净利润/主营业务收入)
//                stockDomain.getFrDomain().setNetMargin(extendDomainArray[5].trim());
//
//                list.add(stockDomain);
//            }
//        } catch (IOException x) {
//            log.error(String.format("IOException: %s%n", x));
//        }
//
//        return list;
//    }
//
//    // 读 财务指标
//    public List<StockDomain> readFinancialReportStep2() {
//
//        List<StockDomain> list = new ArrayList<>();
//        Path path = Paths.get(FR_STEP2_DATA_FILE);
//
//        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                log.debug("read fr step2 data >> {}", line);
//
//                StockDomain stockDomain = new StockDomain();
//                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());
//
//                // 公司代码
//                stockDomain.setCompanyCode(extendDomainArray[0].trim());
//                // 公司简称
//                stockDomain.setCompanyName(extendDomainArray[1].trim());
//                // 截止日期
//                stockDomain.getFrDomain().setDeadline(extendDomainArray[2].trim());
//                // 主营业务收入增长率(%)
//                stockDomain.getFrDomain().setMainBusinessIncomeGrowthRate(extendDomainArray[3].trim());
//                // 净利润增长率(%)
//                stockDomain.getFrDomain().setNetProfitGrowthRate(extendDomainArray[4].trim());
//                // 净资产增长率(%)
//                stockDomain.getFrDomain().setNetAssetGrowthRate(extendDomainArray[5].trim());
//                // 总资产增长率(%)
//                stockDomain.getFrDomain().setTotalAssetsGrowthRate(extendDomainArray[6].trim());
//
//                list.add(stockDomain);
//            }
//        } catch (IOException x) {
//            log.error(String.format("IOException: %s%n", x));
//        }
//
//        return list;
//    }

    // 写 财报
    public void writeFinancialReport2(List<FinancialReport2Domain> data) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_FINANCIAL_REPORT2.getString()), StandardCharsets.UTF_8)) {
            for (FinancialReport2Domain stockDomain : data) {
                String s = stockDomain.builder() + "\r\n";
                log.debug("write fr data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

//    // TODO 前后端分离，数据爬取失败
//    // 读 财务报表
//    public List<FinancialReport2Domain> readFinancialReport2() {
//
//        List<FinancialReport2Domain> list = new ArrayList<>();
//        Path path = Paths.get(FR_EASTMONEY_DATA_FILE);
//
//        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                log.debug("read fr eastmoney data >> {}", line);
//
//                FinancialReport2Domain financialDomain = new FinancialReport2Domain();
//                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());
//
//                // 公司代码
//                financialDomain.setCompanyCode(extendDomainArray[0].trim());
//                // 公司简称
//                financialDomain.setCompanyName(extendDomainArray[1].trim());
//                // 交易日
//                financialDomain.setDate(extendDomainArray[2].trim());
//                // 股价
//                financialDomain.setPrice(extendDomainArray[3].trim());
//                // 截止日期
//                financialDomain.setDeadline(extendDomainArray[4].trim());
//                // 主营业务收入
//                financialDomain.setMainBusinessIncome(extendDomainArray[5].trim());
//                // 净利润
//                financialDomain.setNetProfit(extendDomainArray[6].trim());
//                // 净利润率(净利润/主营业务收入)
//                financialDomain.setNetMargin(extendDomainArray[7].trim());
//                // 主营业务收入增长率(%)
//                financialDomain.setMainBusinessIncomeGrowthRate(extendDomainArray[8].trim());
//                // 净利润增长率(%)
//                financialDomain.setNetProfitGrowthRate(extendDomainArray[9].trim());
//                // 主营业务收入增长率(%)(环比)
//                financialDomain.setMainBusinessIncomeGrowthRateMoM(extendDomainArray[10].trim());
//                // 净利润增长率(%)(环比)
//                financialDomain.setNetProfitGrowthRateMoM(extendDomainArray[11].trim());
//                // 每股收益(元)
//                financialDomain.setBasicEps(extendDomainArray[12].trim());
//                // 每股收益(扣除)(元)
//                financialDomain.setCutBasicEps(extendDomainArray[13].trim());
//                // 每股净资产(元)
//                financialDomain.setBps(extendDomainArray[14].trim());
//                // 净资产收益率(%)
//                financialDomain.setRoeWeighted(extendDomainArray[15].trim());
//                // 每股经营现金流量(元)
//                financialDomain.setPerShareCashFlowFromOperations(extendDomainArray[16].trim());
//                // 销售毛利率(%)
//                financialDomain.setGrossProfitMargin(extendDomainArray[17].trim());
//                // 利润分配
//                financialDomain.setProfitDistribution(extendDomainArray[18].trim());
//                // 股息率(%)
//                financialDomain.setDividendYield(extendDomainArray[19].trim());
//                // 首次公告日期
//                financialDomain.setFirstNoticeDate(extendDomainArray[20].trim());
//                // 最新公告日期
//                financialDomain.setLatestNoticeDate(extendDomainArray[21].trim());
//
//                list.add(financialDomain);
//            }
//        } catch (IOException x) {
//            log.error(String.format("IOException: %s%n", x));
//        }
//
//        return list;
//    }
//
//    // 写 财报
//    public void writeFinancialReport2(List<FinancialReport2Domain> data) {
//
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FR_DATA2_FILE), StandardCharsets.UTF_8)) {
//            for (FinancialReport2Domain stockDomain : data) {
//                String s = stockDomain.frBuilder() + "\r\n";
//                log.debug("write fr data >> {}", s);
//                writer.write(s, 0, s.length());
//            }
//        } catch (IOException x) {
//            log.error(String.format("IOException: %s%n", x));
//        }
//    }

    // 读 分红
    public List<StockDomain> readShareBonus1() {

        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(getFilePath() + Constants.FILE_SHARE_BONUS1.getString());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read sb data >> {}", line);

                StockDomain stockDomain = new StockDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());

                // 公司代码
                stockDomain.setCompanyCode(extendDomainArray[0].trim());
                // 公司简称
                stockDomain.setCompanyName(extendDomainArray[1].trim());
                // 公告日期
                stockDomain.getSbDomain().setBonusDate(extendDomainArray[2].trim());
                // 派息(税前)(元)
                stockDomain.getSbDomain().setDividend(extendDomainArray[3].trim());
                // 进度
                stockDomain.getSbDomain().setSchedule(extendDomainArray[4].trim());
                // 除权除息日
                stockDomain.getSbDomain().setDividendDate(extendDomainArray[5].trim());
                // 股权登记日
                stockDomain.getSbDomain().setRegistrationDate(extendDomainArray[6].trim());

                list.add(stockDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }

    // 读 分红
    public List<StockDomain> readShareBonus() {

        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(getFilePath() + Constants.FILE_SHARE_BONUS.getString());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("read sb data >> {}", line);

                StockDomain stockDomain = new StockDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());

                // 公司代码
                stockDomain.setCompanyCode(extendDomainArray[0].trim());
                // 公司简称
                stockDomain.setCompanyName(extendDomainArray[1].trim());
                // 公告日期
                stockDomain.getSbDomain().setBonusDate(extendDomainArray[2].trim());
                // 派息(税前)(元)
                stockDomain.getSbDomain().setDividend(extendDomainArray[3].trim());
                // 进度
                stockDomain.getSbDomain().setSchedule(extendDomainArray[4].trim());
                // 除权除息日
                stockDomain.getSbDomain().setDividendDate(extendDomainArray[5].trim());
                // 股权登记日
                stockDomain.getSbDomain().setRegistrationDate(extendDomainArray[6].trim());
                // 股权登记日
                stockDomain.getSbDomain().setDividendYear(extendDomainArray[7].trim());

                list.add(stockDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }

    // 写 财报
    public void writeShareBonus(List<StockDomain> data) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_SHARE_BONUS.getString()), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.sbBuilder() + "\r\n";
                log.debug("write sb data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    // 读 历史行情
    public List<DayLineDomain> readHhqData() {
        List<DayLineDomain> list = new ArrayList<>();
        Path path = Paths.get(getFilePath() + Constants.FILE_HHQ.getString());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("read hhq data >> {}", line);

                DayLineDomain stockDomain = new DayLineDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());

                // 公司代码
                stockDomain.setCompanyCode(extendDomainArray[0].trim());
                // 公司简称
                stockDomain.setCompanyName(extendDomainArray[1].trim());
                // 收盘日
                stockDomain.setDay(extendDomainArray[2].trim());
                // 股价
                stockDomain.setPrice(extendDomainArray[3].trim());

                list.add(stockDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
        return list;
    }

    // 写 历史行情
    public void writeHhqData(List<DayLineDomain> data) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_HHQ.getString()), StandardCharsets.UTF_8)) {
            for (DayLineDomain domain : data) {
                String s = domain.hhqBuilder() + "\r\n";
                log.debug("write hhq data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }
}
