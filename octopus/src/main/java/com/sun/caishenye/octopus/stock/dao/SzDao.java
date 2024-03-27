package com.sun.caishenye.octopus.stock.dao;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.component.CacheComponent;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class SzDao {

    @Autowired
    private CacheComponent cache;

    public List<StockDomain> readBaseData() {

        List<StockDomain> list = new ArrayList<>();
        Path path = Paths.get(cache.getFilePath() + Constants.FILE_STOCK_BASE_SZ.getString());

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
//                // 排除B股
//                if (line.contains("B") || line.contains("Ｂ")) {
//                    continue;
//                }
                count++;

                StockDomain stockDomain = new StockDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());

                // 公司代码
                stockDomain.setCompanyCode(extendDomainArray[0].trim());
                // 公司简称
                stockDomain.setCompanyName(extendDomainArray[1].trim());
                // 上市日期
                stockDomain.setListingDate(extendDomainArray[7].trim());
                // 证券交易所
                stockDomain.setExchange(Constants.EXCHANGE_SZ.getString());

                list.add(stockDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }
}
