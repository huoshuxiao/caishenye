package com.sun.caishenye.octopus.fund.business.api;

import com.sun.caishenye.octopus.common.Constans;
import com.sun.caishenye.octopus.fund.domain.MorningStarDetailDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Slf4j
@Service
public class MorningStarRestService {

    protected final String DATA_FILE_NAME_EXTEND = "data/MorningStarExtend.log";
//    protected final String DATA_FILE_NAME_BASE = "data/MorningStarBase.log";
    protected final String DATA_FILE_NAME_DETAIL = "data/MorningStarDetail.log";
    private final Double BASE_DAY = 360d;

    private MorningStarRestTemplate morningStarRestTemplate;
    @Autowired
    public MorningStarRestService(MorningStarRestTemplate morningStarRestTemplate) {
        this.morningStarRestTemplate = morningStarRestTemplate;
    }

    public void run() {
        List<MorningStarExtendDomain> readDataList = readExtendData(DATA_FILE_NAME_EXTEND);
        List<MorningStarDomain> writeDataList = new ArrayList<>(readDataList.size());
        for (MorningStarExtendDomain extendDomain: readDataList) {
            // call rest service
            MorningStarDetailDomain morningStarDetailDomain = morningStarRestTemplate.getManageForObject("manage", extendDomain);
//            CompletableFuture.anyOf(morningStarDetailDomain);
            MorningStarDomain morningStarDomain = new MorningStarDomain();
            morningStarDomain.setMorningStarBaseDomain(extendDomain);
            morningStarDomain.setMorningStarDetailDomain(morningStarDetailDomain);
            writeDataList.add(morningStarDomain);
            log.debug("morningStarDetailDomain value :: {}", morningStarDetailDomain.toString());
        }
        writeData(DATA_FILE_NAME_DETAIL, writeDataList);
    }

    protected List<MorningStarExtendDomain> readExtendData(String fileName) {
        List<MorningStarExtendDomain> list = new ArrayList<>();
        Path path = Paths.get(fileName);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read data {}", line);
                String[] extendDomainArray = line.split(Constans.DELIMITING_COMMA.getCode());
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

    protected void writeData(String fileName, List<MorningStarDomain> data) {

//        Path pathSource = Paths.get(DATA_FILE_NAME_BASE);
//        Path pathTarget = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName)/*Files.copy(pathSource, pathTarget)*/, StandardCharsets.UTF_8)) {
            for (MorningStarDomain morningStarDomain: data) {
                // 计算 年平均回报(%)
                MorningStarExtendDomain extendDomain = new MorningStarExtendDomain();
                extendDomain.setReturnAvg(calReturnAvg(morningStarDomain));
                morningStarDomain.setMorningStarExtendDomain(extendDomain);
                String s = morningStarDomain.toStr() + "\r\n";
                log.debug("write data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    // 计算 年平均回报(%)
    private String calReturnAvg(MorningStarDomain morningStarDomain) {

        log.debug("calReturnAvg data {}", morningStarDomain.toString());

        // 设立以来(%)
        String returnInception = morningStarDomain.getMorningStarBaseDomain().getReturnInception();
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
        return String.format("%.2f", returnAvg);
    }
}
