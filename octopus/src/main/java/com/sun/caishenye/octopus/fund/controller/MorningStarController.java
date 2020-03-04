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

    // step1: 基础数据
    @GetMapping("base")
    public Object base() {
        return morningstarService.base();
    }

    // step2: 扩展数据：基金ID，供采集明细用
    @GetMapping("extend")
    public Object extend() {
        return morningstarService.extend();
    }

    // 生成最终数据 step4: 扩展数据： 明细数据/年平均回报/风险/净值日期/单位净值
    @GetMapping("detail")
    public Object detail() {
        return morningstarService.detail();
    }
}
