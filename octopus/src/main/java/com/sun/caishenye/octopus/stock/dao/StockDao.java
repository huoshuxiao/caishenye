package com.sun.caishenye.octopus.stock.dao;

import com.sun.caishenye.octopus.common.Constants;
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

    protected final String HQ_DATA_FILE = "data/HQ.csv";
    protected final String SB_DATA_FILE = "data/ShareBonus.csv";
    protected final String MM_DATA_FILE = "data/MoneyMoney.csv";

    public void writeMoneyMoney(List<StockDomain> data) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(MM_DATA_FILE), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.toMmStr() + "\r\n";
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
                String s = stockDomain.toHqStr() + "\r\n";
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
                // 股价
                stockDomain.setPrice(extendDomainArray[2].trim());

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
}
