package com.sun.caishenye.octopus.fund.service;

import com.sun.caishenye.octopus.fund.business.api.MorningStarRestService;
import com.sun.caishenye.octopus.fund.business.webmagic.MorningStarBasePageProcessor;
import com.sun.caishenye.octopus.fund.business.webmagic.MorningStarExtendPageProcessor;
import com.sun.caishenye.octopus.fund.dao.MorningStarDao;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MorningStarService {

    @Autowired
    private EastMoneyService eastMoneyService;
    @Autowired
    private MorningStarDao morningStarDao;
    @Autowired
    private MorningStarRestService morningStarRestService;
    @Autowired
    private MorningStarBasePageProcessor morningStarBasePageProcessor;
    @Autowired
    private MorningStarExtendPageProcessor morningStarExtendPageProcessor;
//    @Autowired
//    public MorningStarService(MorningStarBasePageProcessor morningStarBasePageProcessor,
//                              MorningStarExtendPageProcessor morningStarExtendPageProcessor,
//                              MorningStarRestService morningStarRestService,
//                              MorningStarDao morningStarDao,
//                              EastMoneyService eastMoneyService) {
//
//        this.morningStarBasePageProcessor = morningStarBasePageProcessor;
//        this.morningStarExtendPageProcessor = morningStarExtendPageProcessor;
//        this.morningStarRestService = morningStarRestService;
//        this.morningStarDao = morningStarDao;
//        this.eastMoneyService = eastMoneyService;
//    }

    public Object base() {
        try {
            morningStarBasePageProcessor.run();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "finished";
    }

    public Object extend() {
        try {
            morningStarExtendPageProcessor.run();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "finished";
    }

    public Object detail() {
        try {
            Map<String, EastMoneyDetailDomain> eastMoneyMap = eastMoneyService.readDetailDataMap();
            List<MorningStarExtendDomain> readDataList = readExtendData();
            // 补全数据
            List<MorningStarDomain> writeDataList = morningStarRestService.run(readDataList);
            writeData(writeDataList, eastMoneyMap);
        } catch (Exception e) {
            return e;
        }
        return "finished";
    }

    public List<MorningStarExtendDomain> readExtendData() {
        return morningStarDao.readExtendData();
    }

    public void writeData(List<MorningStarDomain> writeDataList, Map<String, EastMoneyDetailDomain> eastMoneyMap) {
        morningStarDao.writeData(writeDataList, eastMoneyMap);
    }
}
