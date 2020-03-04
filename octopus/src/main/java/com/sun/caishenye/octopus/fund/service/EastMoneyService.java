package com.sun.caishenye.octopus.fund.service;

import com.sun.caishenye.octopus.fund.business.webmagic.EastMoneyDetailPageProcessor;
import com.sun.caishenye.octopus.fund.dao.EastMoneyDao;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 天天基金网
 */
@Slf4j
@Service
public class EastMoneyService {

    // 基金净值_估值_行情走势—天天基金网
    protected final String BASE_URL = "http://fund.eastmoney.com/{fundCode}.html";

    @Autowired
    private EastMoneyDao eastMoneyDao;
    @Autowired
    private MorningStarService morningStarService;
    @Autowired
    private EastMoneyDetailPageProcessor eastMoneyDetailPageProcessor;
//    @Autowired
//    public EastMoneyService(EastMoneyDetailPageProcessor eastMoneyDetailPageProcessor,
//                            MorningStarService morningStarService,
//                            EastMoneyDao eastMoneyDao) {
//        this.eastMoneyDetailPageProcessor = eastMoneyDetailPageProcessor;
//        this.morningStarService = morningStarService;
//        this.eastMoneyDao = eastMoneyDao;
//    }

    public Object detail() {
        try {
            List<MorningStarExtendDomain> readDataList = morningStarService.readExtendData();
            List<String> urls = new ArrayList<>(readDataList.size());
            for (MorningStarExtendDomain morningStarExtendDomain : readDataList) {
                urls.add(BASE_URL.replace("{fundCode}", morningStarExtendDomain.getFundCode()));
            }
            // 根据基金code采集数据
            eastMoneyDetailPageProcessor.run(urls);
        } catch (Exception e) {
            log.error("" + e);
            return e;
        }
        return "finished";
    }

    public Map<String, EastMoneyDetailDomain> readDetailDataMap() {
        return eastMoneyDao.readDetailDataMap();
    }

}
