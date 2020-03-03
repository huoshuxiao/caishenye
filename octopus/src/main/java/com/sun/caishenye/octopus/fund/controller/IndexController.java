package com.sun.caishenye.octopus.fund.controller;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class IndexController {

    // 服务器推送事件（Server-Sent Events，SSE）
    // 在 WebFlux 中创建 SSE 的服务器端是非常简单的。只需要返回的对象的类型是 Flux<ServerSentEvent>，就会被自动按照 SSE 规范要求的格式来发送响应。
    @GetMapping("/")
    public Flux<ServerSentEvent<Integer>> randomNumbers() {
        return Flux.interval(Duration.ofSeconds(1000))
                .map(seq -> Tuples.of(seq, ThreadLocalRandom.current().nextInt()))
                .map(data -> ServerSentEvent.<Integer>builder()
                        .event("random")
                        .id(Long.toString(data.getT1()))
                        .data(data.getT2())
                        .build());
    }
}
