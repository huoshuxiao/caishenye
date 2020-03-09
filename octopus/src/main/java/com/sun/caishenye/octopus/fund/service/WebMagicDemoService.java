package com.sun.caishenye.octopus.fund.service;

import com.sun.caishenye.octopus.fund.business.webmagic.WebMagicProcessorDemo;
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
        processor.run();
        return "OK";
    }
}
