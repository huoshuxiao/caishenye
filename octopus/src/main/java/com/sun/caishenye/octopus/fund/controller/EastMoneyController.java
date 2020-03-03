package com.sun.caishenye.octopus.fund.controller;

import com.sun.caishenye.octopus.fund.service.EastMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("eastmoney/")
public class EastMoneyController {

    private EastMoneyService eastMoneyService;

    @Autowired
    public EastMoneyController(EastMoneyService eastMoneyService) {
        this.eastMoneyService = eastMoneyService;
    }

    @GetMapping("detail")
    public Object detail() {
        return eastMoneyService.detail();
    }
}
