package com.sun.caishenye.octopus.html.service;

import com.sun.caishenye.octopus.html.component.MorningStarPageProcessor;
import com.sun.caishenye.octopus.html.component.WebmagicGithubPageProcessorDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MorningStarService {

//    private final WebmagicGithubPageProcessorDemo githubRepo;
    private final MorningStarPageProcessor processor;

    @Autowired
    public MorningStarService(MorningStarPageProcessor processor) {
        this.processor = processor;
    }

    public Object run() {

        try {
            processor.run();
        } catch (Exception e) {
            return e.getMessage();
        }

        return "OK";
    }
}
