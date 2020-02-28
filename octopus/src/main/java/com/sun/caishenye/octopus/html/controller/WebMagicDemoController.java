package com.sun.caishenye.octopus.html.controller;

import com.sun.caishenye.octopus.html.service.MorningStarService;
import com.sun.caishenye.octopus.html.service.WebMagicDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo/")
public class WebMagicDemoController {

    private final WebMagicDemoService morningstarService;

    // @Autowired (to do constructor injection) works well
    @Autowired
    public WebMagicDemoController(WebMagicDemoService morningstarService) {
        this.morningstarService = morningstarService;
    }

    @GetMapping("web")
    public Object web() {
        return morningstarService.run();
    }
}
