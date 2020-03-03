package com.sun.caishenye.octopus.fund.controller;

import com.sun.caishenye.octopus.fund.service.MorningStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("morningstar/")
public class MorningStarController {

    private final MorningStarService morningstarService;

    // @Autowired (to do constructor injection) works well
    @Autowired
    public MorningStarController(MorningStarService morningstarService) {
        this.morningstarService = morningstarService;
    }

    @GetMapping("base")
    public Object base() {
        return morningstarService.base();
    }

    @GetMapping("extend")
    public Object extend() {
        return morningstarService.extend();
    }

    @GetMapping("detail")
    public Object detail() {
        return morningstarService.detail();
    }
}
