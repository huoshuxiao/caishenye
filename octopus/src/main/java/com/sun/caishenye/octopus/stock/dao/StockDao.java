package com.sun.caishenye.octopus.stock.dao;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.stock.domain.DayLineDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
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

    private final String FILE_PATH = "data/";
    protected final String HQ_DATA_FILE = FILE_PATH + Constants.FILE_HQ.getString();
    protected final String HHQ_DATA_FILE = FILE_PATH + Constants.FILE_HHQ.getString();
    protected final String SB_DATA_FILE = FILE_PATH + Constants.FILE_SHARE_BONUS.getString();
    protected final String MM_DATA_FILE = FILE_PATH + Constants.FILE_MONEY_MONEY.getString();

    public void writeMoneyMoney(List<StockDomain> data) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(MM_DATA_FILE), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.mmBuilder() + "\r\n";
                log.debug("write mm data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    public void writeHqData(List<StockDomain> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(HQ_DATA_FILE), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.hqBuilder() + "\r\n";
                log.debug("write hq data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    public List<StockDomain> readHqData() {
        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(HQ_DATA_FILE);

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

    public List<StockDomain> readShareBonus() {

        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(SB_DATA_FILE);

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

    // 读 历史行情
    public List<DayLineDomain> readHhqData() {
        List<DayLineDomain> list = new ArrayList<>();
        Path path = Paths.get(HHQ_DATA_FILE);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
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

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(HHQ_DATA_FILE), StandardCharsets.UTF_8)) {
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
