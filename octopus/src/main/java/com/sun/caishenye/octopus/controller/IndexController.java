package com.sun.caishenye.octopus.controller;

import com.sun.caishenye.octopus.fund.service.EastMoneyService;
import com.sun.caishenye.octopus.fund.service.FundService;
import com.sun.caishenye.octopus.fund.service.MorningStarService;
import com.sun.caishenye.octopus.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/")
public class IndexController {

    // 服务器推送事件（Server-Sent Events，SSE）
    // 在 WebFlux 中创建 SSE 的服务器端是非常简单的。只需要返回的对象的类型是 Flux<ServerSentEvent>，就会被自动按照 SSE 规范要求的格式来发送响应。
    @GetMapping("")
    public Flux<ServerSentEvent<Integer>> randomNumbers() {
        return Flux.interval(Duration.ofSeconds(1000))
                .map(seq -> Tuples.of(seq, ThreadLocalRandom.current().nextInt()))
                .map(data -> ServerSentEvent.<Integer>builder()
                        .event("random")
                        .id(Long.toString(data.getT1()))
                        .data(data.getT2())
                        .build());
    }

    ////////////////////////////////////////// fund ////////////////////////////////////////////////////////////////////
    @Autowired
    private MorningStarService morningstarService;

    @Autowired
    private EastMoneyService eastMoneyService;

    // step1: 基础数据
    @GetMapping("morningstar/base")
    public Object base() {
        return morningstarService.base();
    }

    // step2: 扩展数据：基金ID，供采集明细用
    @GetMapping("morningstar/extend")
    public Object extend() {
        return morningstarService.extend();
    }

    // 生成最终数据 step4: 组合数据 晨星 + 东方财富
    @GetMapping("morningstar/detail")
    public Object detail() {
        return morningstarService.detail();
    }

    // step3: 扩展数据：友情提示 风险/净值日期/单位净值/基金类型/基金规模，供生成数据用
    @GetMapping("eastmoney/detail")
    public Object detai2l() {
        return eastMoneyService.detail();
    }

    @Autowired
    private FundService fundService;
    // run
    @GetMapping("fund")
    public Object fund() {
        LocalDateTime startTime = LocalDateTime.now();
        fundService.run();
        LocalDateTime endTime = LocalDateTime.now();
        return "fund " + ChronoUnit.MINUTES.between(startTime, endTime);
    }

    //////////////////////////////////////////////////// stock /////////////////////////////////////////////////////////
    @Autowired
    private StockService stockService;

    // step1: 基础数据 (公司代码/公司简称)
    @GetMapping("base")
    public Object stockBase() throws ExecutionException, InterruptedException {
        return stockService.base();
    }

    // step2: 财务报表
    @GetMapping("fr")
    public Object financialReport() throws ExecutionException, InterruptedException {
        return stockService.financialReport();
    }

    // step2: 分红配股
    @GetMapping("sb")
    public Object shareBonus() {
        return stockService.shareBonus();
    }

    // step2: 实时行情
    @GetMapping("hq")
    public Object hq() throws ExecutionException, InterruptedException {
        return stockService.hq();
    }
    // step2: 历史行情
    @GetMapping("hhq")
    public Object hhq() throws ExecutionException, InterruptedException {
        return stockService.hhq();
    }

    // step3: 钱多多
    @GetMapping("mm")
    public Object moneyMoney() {
        LocalDateTime startTime = LocalDateTime.now();
        String tag = stockService.moneyMoney().toString();
        LocalDateTime endTime = LocalDateTime.now();
        return tag + " " + ChronoUnit.MINUTES.between(startTime, endTime);
    }

    // run
    @GetMapping("stock")
    public Object stock() throws ExecutionException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        stockService.run();
        LocalDateTime endTime = LocalDateTime.now();
        return "stock " + ChronoUnit.MINUTES.between(startTime, endTime);
    }
}
