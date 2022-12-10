package com.sun.caishenye.octopus.fund.service;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.fund.agent.api.EastMoneyRestTemplate;
import com.sun.caishenye.octopus.fund.agent.webmagic.EastMoneyDetailPageProcessor;
import com.sun.caishenye.octopus.fund.dao.EastMoneyDao;
import com.sun.caishenye.octopus.fund.domain.EastMoneyBaseDomain;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 天天基金网
 */
@Slf4j
@Service
public class EastMoneyService {

    // 基金净值_估值_行情走势—天天基金网
    protected final String BASE_URL = "http://fund.eastmoney.com/{fundCode}.html";

//    @Autowired
//    private MorningStarService morningStarService;

    @Autowired
    private EastMoneyDetailPageProcessor eastMoneyDetailPageProcessor;

    @Autowired
    private EastMoneyRestTemplate eastMoneyRestTemplate;

    @Autowired
    private EastMoneyDao eastMoneyDao;

//    @Autowired
//    public EastMoneyService(EastMoneyDetailPageProcessor eastMoneyDetailPageProcessor,
//                            MorningStarService morningStarService,
//                            EastMoneyDao eastMoneyDao) {
//        this.eastMoneyDetailPageProcessor = eastMoneyDetailPageProcessor;
//        this.morningStarService = morningStarService;
//        this.eastMoneyDao = eastMoneyDao;
//    }

    public Object base() {

        List<String> data5Year = eastMoneyRestTemplate.getBaseData(5);
        List<String> data10Year = eastMoneyRestTemplate.getBaseData(10);

        List<EastMoneyBaseDomain> data = merge(data5Year, data10Year);

        eastMoneyDao.writeBaseData(data);
        return "finished";
    }

    private List<EastMoneyBaseDomain> merge(List<String> data5Year, List<String> data10Year) {
        log.debug("East Money 5 year data count {}", data5Year.size());
        log.debug("East Money 10 year data count {}", data10Year.size());

        data10Year = data10Year.stream().sorted().collect(Collectors.toList());
        data5Year = data5Year.stream().sorted().collect(Collectors.toList());

        List<EastMoneyBaseDomain> baseList = new ArrayList<>();
        for (int i = 0; i < data10Year.size(); i++) {

            List<String> data10 = Arrays.asList(data10Year.get(i).split(Constants.DELIMITING_COMMA.getString()));
            List<String> data5 = Arrays.asList(data5Year.get(i).split(Constants.DELIMITING_COMMA.getString()));
            log.debug("East Money base data 10 year {}>>{}-{} :: {}", i, data10.get(0), data10.get(1), data10.size());
            log.debug("East Money base data 5 year {}>>{}-{} :: {}", i, data5.get(0), data5.get(1), data5.size());

            EastMoneyBaseDomain baseDomain = new EastMoneyBaseDomain();
            // 基金代码
            baseDomain.setFundCode(data10.get(0));
            // 基金名称
            baseDomain.setFundName(data10.get(1));
            // 成立日期
            baseDomain.setInceptionDate(data10.get(16));
            // 净值日期
            baseDomain.setClosePriceDate(data10.get(3));
            // 单位净值
            baseDomain.setClosePrice(data10.get(4));
            // 今年以来(%)
            baseDomain.setReturnThisYear(data10.get(14));
            // 1天回报(%)
            baseDomain.setReturn1Day(data10.get(6));
            // 1周回报(%)
            baseDomain.setReturn1Week(data10.get(7));
            // 1个月回报(%)
            baseDomain.setReturn1Month(data10.get(8));
            // 3个月回报(%)
            baseDomain.setReturn3Month(data10.get(9));
            // 6个月回报(%)
            baseDomain.setReturn6Month(data10.get(10));
            // 1年回报(%)
            baseDomain.setReturn1Year(data10.get(11));
            // 2年回报(%)
            baseDomain.setReturn2Year(data10.get(12));
            // 3年回报(%)
            baseDomain.setReturn3Year(data10.get(13));
            // 5年回报(%)
            // 数据无 跳过
            log.debug("5年回报 {}", String.join(",", data5));
            if (data5.size() > 18 && StringUtils.isNotEmpty(data5.get(16)) && LocalDate.parse(data5.get(16)).until(LocalDate.now(), ChronoUnit.YEARS) >= 5) {
                baseDomain.setReturn5Year(data5.get(18));
            }
            // 10年回报(%)
            // 数据无 跳过
            log.debug("10年回报 {}", String.join(",", data10));
            if (data10.size() > 18 && StringUtils.isNotEmpty(data10.get(16)) && LocalDate.parse(data10.get(16)).until(LocalDate.now(), ChronoUnit.YEARS) >= 10) {
                baseDomain.setReturn10Year(data10.get(18));
            }
            // 设立以来(%)
            baseDomain.setReturnInception(data10.get(15));
            // 累计净值
            baseDomain.setTotalPrice((StringUtils.isEmpty(data10.get(5)) ? "-" : data10.get(5)));
            baseList.add(baseDomain);
        }
        return baseList;
    }

    public Object detail() {
        List<EastMoneyBaseDomain> readDataList = readBaseData();
        List<String> urls = new ArrayList<>(readDataList.size());
        readDataList.forEach(t -> {
            urls.add(BASE_URL.replace("{fundCode}", t.getFundCode()));
        });

        // 根据基金code采集数据
        eastMoneyDetailPageProcessor.run(urls);

        return "finished";
    }

    public List<EastMoneyBaseDomain> readBaseData() {
        return eastMoneyDao.readBaseData();
    }

    public List<EastMoneyDetailDomain> readDetailData() {
        return eastMoneyDao.readDetailData();
    }

//    public Map<String, EastMoneyDetailDomain> readDetailDataMap() {
//        return eastMoneyDao.readDetailDataMap();
//    }
}
