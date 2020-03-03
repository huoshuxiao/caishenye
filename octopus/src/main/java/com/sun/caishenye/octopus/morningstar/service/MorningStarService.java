package com.sun.caishenye.octopus.morningstar.service;

import com.sun.caishenye.octopus.morningstar.business.api.MorningStarRestService;
import com.sun.caishenye.octopus.morningstar.business.api.MorningStarRestTemplate;
import com.sun.caishenye.octopus.morningstar.business.webmagic.MorningStarBasePageProcessor;
import com.sun.caishenye.octopus.morningstar.business.webmagic.MorningStarExtendPageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MorningStarService {

    private final MorningStarRestService morningStarRestService;
    private final MorningStarBasePageProcessor morningStarBasePageProcessor;
    private final MorningStarExtendPageProcessor morningStarExtendPageProcessor;
    @Autowired
    public MorningStarService(MorningStarBasePageProcessor morningStarBasePageProcessor,
                              MorningStarExtendPageProcessor morningStarExtendPageProcessor,
                              MorningStarRestService morningStarRestService) {

        this.morningStarBasePageProcessor = morningStarBasePageProcessor;
        this.morningStarExtendPageProcessor = morningStarExtendPageProcessor;
        this.morningStarRestService = morningStarRestService;
    }

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
            morningStarRestService.run();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "finished";
    }
}
