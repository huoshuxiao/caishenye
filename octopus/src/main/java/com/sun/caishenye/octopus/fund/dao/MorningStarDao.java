package com.sun.caishenye.octopus.fund.dao;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
import com.sun.caishenye.octopus.fund.domain.FundDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class MorningStarDao {

    protected final String DATA_FILE_NAME_EXTEND = "data/MorningStarExtend.log";
    protected final String DATA_FILE_NAME = "data/Fund.csv";
    private final Double BASE_DAY = 360d;

    public List<MorningStarExtendDomain> readExtendData() {
        List<MorningStarExtendDomain> list = new ArrayList<>();
        Path path = Paths.get(DATA_FILE_NAME_EXTEND);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read data >> {}", line);
                String[] extendDomainArray = line.split(Constants.DELIMITING_12.getString());
                MorningStarExtendDomain extendDomain = new MorningStarExtendDomain();
                // 基金ID
                extendDomain.setFundId(extendDomainArray[0]);
                // 基金代码
                extendDomain.setFundCode(extendDomainArray[1]);
                // 基金名称
                extendDomain.setFundName(extendDomainArray[2]);
                // 1天回报(%)
                extendDomain.setReturn1Day(extendDomainArray[3]);
                // 1周回报(%)
                extendDomain.setReturn1Week(extendDomainArray[4]);
                // 1个月回报(%)
                extendDomain.setReturn1Month(extendDomainArray[5]);
                // 3个月回报(%)
                extendDomain.setReturn3Month(extendDomainArray[6]);
                // 6个月回报(%)
                extendDomain.setReturn6Month(extendDomainArray[7]);
                // 1年回报(%)
                extendDomain.setReturn1Year(extendDomainArray[8]);
                // 2年回报(%)
                extendDomain.setReturn2Year(extendDomainArray[9]);
                // 3年回报(%)
                extendDomain.setReturn3Year(extendDomainArray[10]);
                // 5年回报(%)
                extendDomain.setReturn5Year(extendDomainArray[11]);
                // 10年回报(%)
                extendDomain.setReturn10Year(extendDomainArray[12]);
                // 设立以来(%)
                extendDomain.setReturnInception(extendDomainArray[13]);
                list.add(extendDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
        return list;
    }

    /**
     * 补全数据
     *
     * @param data
     * @param eastMoneyMap
     */
    public void writeData(List<FundDomain> data, Map<String, EastMoneyDetailDomain> eastMoneyMap) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE_NAME), StandardCharsets.UTF_8)) {
            for (FundDomain morningStarDomain : data) {
                MorningStarExtendDomain extendDomain = new MorningStarExtendDomain();
                // 计算 年平均回报(%)
                extendDomain.setReturnAvg(calReturnAvg(morningStarDomain));
                EastMoneyDetailDomain eastMoneyDetailDomain = eastMoneyMap.get(morningStarDomain.getMorningStarBaseDomain().getFundCode());
                // 晨星数据在东方财富中不存在
                if (eastMoneyDetailDomain == null) {
                    // 风险
                    extendDomain.setRisk("-");
                    // 净值日期
                    extendDomain.setClosePriceDate("-");
                    // 单位净值
                    extendDomain.setClosePrice("-");
                } else {
                    extendDomain.setRisk(eastMoneyDetailDomain.getRisk().toString());
                    extendDomain.setClosePriceDate(eastMoneyDetailDomain.getClosePriceDate());
                    extendDomain.setClosePrice(eastMoneyDetailDomain.getClosePrice());
                }

                morningStarDomain.setMorningStarExtendDomain(extendDomain);
                String s = morningStarDomain.toStr() + "\r\n";
                log.debug("write data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    // 计算 年平均回报(%)
    private String calReturnAvg(FundDomain morningStarDomain) {

        log.debug("calReturnAvg data {}", morningStarDomain.toString());

        // 设立以来(%)
        String returnInception = Utils.formatNumber2String(morningStarDomain.getMorningStarBaseDomain().getReturnInception());
        // 成立日期
        String inceptionDate = morningStarDomain.getMorningStarDetailDomain().getInceptionDate();

        if ("-".equals(returnInception) || StringUtils.isEmpty(returnInception) || StringUtils.isEmpty(inceptionDate)) {
            return "-";
        }

        // 年平均回报(%) = 设立以来(%) / (当前日期 - 成立日期)
        LocalDate today = LocalDate.now();
        LocalDate inceptionLocalDate = LocalDate.of(Integer.valueOf(inceptionDate.substring(0, 4)).intValue(),
                Integer.valueOf(inceptionDate.substring(5, 7)).intValue(),
                Integer.valueOf(inceptionDate.substring(8, 10)).intValue());
        double returnAvg = Double.valueOf(returnInception) / Double.valueOf(ChronoUnit.DAYS.between(inceptionLocalDate, today) / BASE_DAY);
        return Utils.formatNumber2String(String.format("%.2f", returnAvg));
    }
}
