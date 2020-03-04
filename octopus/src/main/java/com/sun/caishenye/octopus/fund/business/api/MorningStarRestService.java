package com.sun.caishenye.octopus.fund.business.api;

import com.sun.caishenye.octopus.fund.domain.FundDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarDetailDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@Slf4j
@Service
public class MorningStarRestService {

    private MorningStarRestTemplate morningStarRestTemplate;

    @Autowired
    public MorningStarRestService(MorningStarRestTemplate morningStarRestTemplate) {
        this.morningStarRestTemplate = morningStarRestTemplate;
    }

    public List<FundDomain> run(List<MorningStarExtendDomain> readDataList) {
        List<FundDomain> writeDataList = new ArrayList<>(readDataList.size());
        for (MorningStarExtendDomain extendDomain : readDataList) {
            // call rest service
            MorningStarDetailDomain morningStarDetailDomain = morningStarRestTemplate.getManageForObject("manage", extendDomain);
//            CompletableFuture.anyOf(morningStarDetailDomain);
            FundDomain morningStarDomain = new FundDomain();
            morningStarDomain.setMorningStarBaseDomain(extendDomain);
            morningStarDomain.setMorningStarDetailDomain(morningStarDetailDomain);
            writeDataList.add(morningStarDomain);
            log.debug("morningStarDetailDomain value :: {}", morningStarDetailDomain.toString());
        }

        return writeDataList;
    }
}
