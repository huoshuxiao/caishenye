//package com.sun.caishenye.octopus.fund.agent.api;
//
//import com.sun.caishenye.octopus.fund.domain.FundDomain;
//import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//@Slf4j
//@Service
//public class MorningStarRestService {
//
//    private MorningStarRestTemplate morningStarRestTemplate;
//
//    @Autowired
//    public MorningStarRestService(MorningStarRestTemplate morningStarRestTemplate) {
//        this.morningStarRestTemplate = morningStarRestTemplate;
//    }
//
//    public List<FundDomain> run(List<MorningStarExtendDomain> readDataList) {
//        List<FundDomain> writeDataList = new ArrayList<>(readDataList.size());
//        List<CompletableFuture<FundDomain>> futureList = new ArrayList<>();
//        try {
//            for (MorningStarExtendDomain extendDomain : readDataList) {
//                // call rest service
//                CompletableFuture<FundDomain> future = CompletableFuture.supplyAsync(() -> morningStarRestTemplate.getManageForObject("manage", extendDomain)).get();
//                futureList.add(future);
//            }
//
//            for (CompletableFuture<FundDomain> future: futureList) {
//                FundDomain fundDomain = future.get();
//                writeDataList.add(fundDomain);
//            }
//
//        } catch (InterruptedException | ExecutionException e) {
//            log.error("" + e);
//            return null;
//        }
//
//        return writeDataList;
//    }
//}
