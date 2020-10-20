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
public class ShDao {

//
//    public void writeData(List<StockDomain> data) {
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(BASE_DATA_FILE_NAME), StandardCharsets.UTF_8)) {
//            for (StockDomain stockDomain : data) {
//                String s = stockDomain.toStr() + "\r\n";
//                log.debug("write data >> {}", s);
//                writer.write(s, 0, s.length());
//            }
//        } catch (IOException x) {
//            log.error(String.format("IOException: %s%n", x));
//        }
//    }

    public List<StockDomain> readBaseData() {

        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(Constants.FILE_STOCK_BASE_SH.getString());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                log.debug("read data >> {}", line);
                // 排除标题
                if (count == 0) {
                    count++;
                    continue;
                }
                count++;
                StockDomain stockDomain = new StockDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());

                // 公司代码
                stockDomain.setCompanyCode(extendDomainArray[0].trim());
                // 公司简称
                stockDomain.setCompanyName(extendDomainArray[1].trim());
                // 上市日期
                stockDomain.setListingDate(extendDomainArray[4].trim());
                // 证券交易所
                stockDomain.setExchange(Constants.EXCHANGE_SH.getString());

                list.add(stockDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }
}
