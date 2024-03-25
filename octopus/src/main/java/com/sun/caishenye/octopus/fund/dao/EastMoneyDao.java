package com.sun.caishenye.octopus.fund.dao;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.fund.domain.EastMoneyBaseDomain;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class EastMoneyDao {

    private static final String FILE_PATH = Constants.FILE_PATH.getString();;

    private static final String BASE_FILE = FILE_PATH + Constants.FILE_EAST_MONEY_BASE.getString();
    private static final String DATA_FILE_NAME_DETAIL = FILE_PATH + Constants.FILE_EAST_MONEY_DETAIL.getString();

    // 写 基础数据
    public void writeBaseData(List<EastMoneyBaseDomain> data) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(BASE_FILE), StandardCharsets.UTF_8)) {
            for (EastMoneyBaseDomain stockDomain : data) {
                String s = stockDomain.builder() + "\r\n";
                log.debug("write base data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    // 读 基础数据
    public List<EastMoneyBaseDomain> readBaseData() {

        List<EastMoneyBaseDomain> list = new ArrayList<>();
        Path path = Paths.get(BASE_FILE);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read base data >> {}", line);

                EastMoneyBaseDomain baseDomain = new EastMoneyBaseDomain();
                String[] datas = line.split(Constants.DELIMITING_COMMA.getString());
                // 基金代码
                baseDomain.setFundCode(datas[0].trim());
                // 基金名称
                baseDomain.setFundName(datas[1].trim());
                // 成立日期
                baseDomain.setInceptionDate(datas[2].trim());
                // 净值日期
                baseDomain.setClosePriceDate(datas[3].trim());
                // 单位净值
                baseDomain.setClosePrice(datas[4].trim());
                // 今年以来(%)
                baseDomain.setReturnThisYear(datas[5].trim());
                // 1天回报(%)
                baseDomain.setReturn1Day(datas[6].trim());
                // 1周回报(%)
                baseDomain.setReturn1Week(datas[7].trim());
                // 1个月回报(%)
                baseDomain.setReturn1Month(datas[8].trim());
                // 3个月回报(%)
                baseDomain.setReturn3Month(datas[9].trim());
                // 6个月回报(%)
                baseDomain.setReturn6Month(datas[10].trim());
                // 1年回报(%)
                baseDomain.setReturn1Year(datas[11].trim());
                // 2年回报(%)
                baseDomain.setReturn2Year(datas[12].trim());
                // 3年回报(%)
                baseDomain.setReturn3Year(datas[13].trim());
                // 5年回报(%)
                baseDomain.setReturn5Year(datas[14].trim());
                // 10年回报(%)
                baseDomain.setReturn10Year(datas[15].trim());
                // 设立以来(%)
                baseDomain.setReturnInception(datas[16].trim());
                // 累计净值
                baseDomain.setTotalPrice(datas[17].trim());

                list.add(baseDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }

    // 读 详细数据
    public List<EastMoneyDetailDomain> readDetailData() {

        List<EastMoneyDetailDomain> list = new ArrayList<>();
        Path path = Paths.get(DATA_FILE_NAME_DETAIL);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read data >> {}", line);
                EastMoneyDetailDomain eastMoneyDetailDomain = new EastMoneyDetailDomain();
                String[] extendDomainArray = line.split(Constants.DELIMITING_COMMA.getString());

                // 基金代码
                eastMoneyDetailDomain.setFundCode(extendDomainArray[0]);
                // 基金名称
                eastMoneyDetailDomain.setFundName(extendDomainArray[1]);
                // 基金类型
                eastMoneyDetailDomain.setType(extendDomainArray[2]);
                // 基金规模
                eastMoneyDetailDomain.setSize(extendDomainArray[3]);
                // 基金经理
                eastMoneyDetailDomain.setManagerName(extendDomainArray[4]);
                // 管理期间
                eastMoneyDetailDomain.setManagementRange(extendDomainArray[5]);
                // 管理时间
                eastMoneyDetailDomain.setManagementTime(extendDomainArray[6]);
                // 管理回报
                eastMoneyDetailDomain.setManagementReturn(extendDomainArray[7]);
                // 净值日期
                eastMoneyDetailDomain.setClosePriceDate(extendDomainArray[8]);
                // 单位净值
                eastMoneyDetailDomain.setClosePrice(extendDomainArray[9]);
                // 风险
                eastMoneyDetailDomain.setRisk(Boolean.valueOf(extendDomainArray[10]));

                list.add(eastMoneyDetailDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }

//    public Map<String, EastMoneyDetailDomain> readDetailDataMap() {
//        Map<String, EastMoneyDetailDomain> map = new HashMap<>();
//        List<EastMoneyDetailDomain> list = readDetailData();
//        for (EastMoneyDetailDomain detailDomain : list) {
//            map.put(detailDomain.getFundCode(), detailDomain);
//        }
//
//        return map;
//    }
}
