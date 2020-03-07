package com.sun.caishenye.octopus.fund.business.api;

import com.sun.caishenye.octopus.fund.domain.MorningStarDetailDomain;
import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * RestTemplate 采集晨星数据,然后写入
 */
@Component
@Slf4j
public class MorningStarRestTemplate {

    /* http://cn.morningstar.com/handler/quicktake.ashx?command=manage&fcid=0P0000RU7I&randomid=0.020201448060728877 */
    protected final String BASE_URL = "http://cn.morningstar.com/handler/quicktake.ashx?";
    protected final String QUERY_URL = "command={0}&fcid={1}&randomid={2}";

    private RestTemplate restTemplate;

    @Autowired
    public MorningStarRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<MorningStarDetailDomain> getManageForObject(String manage, MorningStarExtendDomain extendDomain) {
        log.debug("extendDomain :: {}", extendDomain);
        // 基金ID
        String fcId = extendDomain.getFundId();

        String url = BASE_URL + QUERY_URL;
        // call rest service
        MorningStarDetailDomain morningStarDetailDomain = restTemplate.getForObject(url, MorningStarDetailDomain.class, urlBuilder(manage, fcId));
        // 异常数据
        if (morningStarDetailDomain == null) {
            morningStarDetailDomain = new MorningStarDetailDomain();
        }
        log.debug("morningStarDetailDomain value :: {}", morningStarDetailDomain);
        return CompletableFuture.completedFuture(morningStarDetailDomain);
    }

    protected Map<String, String> urlBuilder(String manage, String fcid) {
        String randomid = String.valueOf(RandomUtils.nextLong());

        Map<String, String> params = new HashMap<>();
        params.put("0", manage);
        params.put("1", fcid);
        params.put("2", randomid);
        return params;
    }
}
