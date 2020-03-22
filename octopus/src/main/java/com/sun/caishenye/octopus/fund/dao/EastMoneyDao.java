package com.sun.caishenye.octopus.fund.dao;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class EastMoneyDao {

    protected final String DATA_FILE_NAME_DETAIL = "data/EastMoneyDetail.log";

    public List<EastMoneyDetailDomain> readDetailDataList() {

        List<EastMoneyDetailDomain> list = new ArrayList<>();
        Path path = Paths.get(DATA_FILE_NAME_DETAIL);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read data >> {}", line);
                EastMoneyDetailDomain eastMoneyDetailDomain = new EastMoneyDetailDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_12.getString());

                // 基金代码
                eastMoneyDetailDomain.setFundCode(extendDomainArray[0]);
                // 基金名称
                eastMoneyDetailDomain.setFundName(extendDomainArray[1]);
                // 净值日期
                eastMoneyDetailDomain.setClosePriceDate(extendDomainArray[2]);
                // 单位净值
                eastMoneyDetailDomain.setClosePrice(extendDomainArray[3]);
                // 风险
                eastMoneyDetailDomain.setRisk(Boolean.valueOf(extendDomainArray[4]));

                list.add(eastMoneyDetailDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }

    public Map<String, EastMoneyDetailDomain> readDetailDataMap() {
        Map<String, EastMoneyDetailDomain> map = new HashMap<>();
        List<EastMoneyDetailDomain> list = readDetailDataList();
        for (EastMoneyDetailDomain detailDomain : list) {
            map.put(detailDomain.getFundCode(), detailDomain);
        }

        return map;
    }
}
