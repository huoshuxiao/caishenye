package com.sun.caishenye.octopus.morningstar.business.api;

import com.sun.caishenye.octopus.common.Constans;
import com.sun.caishenye.octopus.morningstar.domain.MorningStarDetailDomain;
import com.sun.caishenye.octopus.morningstar.domain.MorningStarDomain;
import com.sun.caishenye.octopus.morningstar.domain.MorningStarExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

/**
 * RestTemplate 采集晨星数据,然后写入
 *
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

//    @Async
    public MorningStarDetailDomain getManageForObject(String manage, MorningStarExtendDomain extendDomain) {

        // 基金ID
        String fcId = extendDomain.getFundId();

        String url = BASE_URL + QUERY_URL;
        // call rest service
        MorningStarDetailDomain morningStarDetailDomain = restTemplate.getForObject(url, MorningStarDetailDomain.class, urlBuilder(manage, fcId));
        log.debug("morningStarDetailDomain value :: {}", morningStarDetailDomain.toString());
        return morningStarDetailDomain;
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
