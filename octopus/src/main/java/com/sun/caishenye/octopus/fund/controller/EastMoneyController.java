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

    // step3: 扩展数据：友情提示 风险/净值日期/单位净值，供生成数据用
    @GetMapping("detail")
    public Object detail() {
        return eastMoneyService.detail();
    }
}
