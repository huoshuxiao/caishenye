//package com.sun.caishenye.octopus.fund.service;
//
//import com.sun.caishenye.octopus.fund.agent.api.MorningStarRestService;
//import com.sun.caishenye.octopus.fund.agent.webmagic.MorningStarBasePageProcessor;
//import com.sun.caishenye.octopus.fund.agent.webmagic.MorningStarExtendPageProcessor;
//import com.sun.caishenye.octopus.fund.dao.MorningStarDao;
//import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
//import com.sun.caishenye.octopus.fund.domain.FundDomain;
//import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//public class MorningStarService {
//
//    @Autowired
//    private EastMoneyService eastMoneyService;
////    @Autowired
////    private MorningStarDao morningStarDao;
////    @Autowired
////    private MorningStarRestService morningStarRestService;
////    @Autowired
////    private MorningStarBasePageProcessor morningStarBasePageProcessor;
////    @Autowired
////    private MorningStarExtendPageProcessor morningStarExtendPageProcessor;
////    @Autowired
////    public MorningStarService(MorningStarBasePageProcessor morningStarBasePageProcessor,
////                              MorningStarExtendPageProcessor morningStarExtendPageProcessor,
////                              MorningStarRestService morningStarRestService,
////                              MorningStarDao morningStarDao,
////                              EastMoneyService eastMoneyService) {
////
////        this.morningStarBasePageProcessor = morningStarBasePageProcessor;
////        this.morningStarExtendPageProcessor = morningStarExtendPageProcessor;
////        this.morningStarRestService = morningStarRestService;
////        this.morningStarDao = morningStarDao;
////        this.eastMoneyService = eastMoneyService;
////    }
//
////    public Object base() {
////        morningStarBasePageProcessor.run();
////        return "finished";
////    }
////
////    public Object extend() {
////        morningStarExtendPageProcessor.run();
////        return "finished";
////    }
////
////    public Object detail() {
////        Map<String, EastMoneyDetailDomain> eastMoneyMap = eastMoneyService.readDetailDataMap();
////        List<MorningStarExtendDomain> readDataList = readExtendData();
////        // 扩展数据： 明细数据
////        List<FundDomain> writeDataList = morningStarRestService.run(readDataList);
////        writeData(writeDataList, eastMoneyMap);
////        return "finished";
////    }
////
////    public List<MorningStarExtendDomain> readExtendData() {
////        return morningStarDao.readExtendData();
////    }
////
////    public void writeData(List<FundDomain> writeDataList, Map<String, EastMoneyDetailDomain> eastMoneyMap) {
////        morningStarDao.writeData(writeDataList, eastMoneyMap);
////    }
//}
