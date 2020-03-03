package com.sun.caishenye.octopus.morningstar.service;

import com.sun.caishenye.octopus.morningstar.business.webmagic.WebMagicProcessorDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebMagicDemoService {

    private final WebMagicProcessorDemo processor;

    @Autowired
    public WebMagicDemoService(WebMagicProcessorDemo processor) {
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
