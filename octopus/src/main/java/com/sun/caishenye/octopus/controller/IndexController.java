package com.sun.caishenye.octopus.controller;

import com.sun.caishenye.octopus.fund.service.EastMoneyService;
import com.sun.caishenye.octopus.fund.service.MorningStarService;
import com.sun.caishenye.octopus.stock.service.ShService;
import com.sun.caishenye.octopus.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
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

    // 生成最终数据 step4: 扩展数据： 明细数据/年平均回报/风险/净值日期/单位净值
    @GetMapping("morningstar/detail")
    public Object detail() {
        return morningstarService.detail();
    }

    // step3: 扩展数据：友情提示 风险/净值日期/单位净值，供生成数据用
    @GetMapping("eastmoney/detail")
    public Object detai2l() {
        return eastMoneyService.detail();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private ShService shangZhengService;

    @Autowired
    private StockService stockService;

    // step1: 基础数据 TODO unuse
    @GetMapping("sh/base")
    public Object shBase() throws ExecutionException, InterruptedException {
        return shangZhengService.base2();
    }

    // step2: 财务报表
    @GetMapping("fr")
    public Object financialReport() throws ExecutionException, InterruptedException {
        return stockService.financialReport();
    }

}
