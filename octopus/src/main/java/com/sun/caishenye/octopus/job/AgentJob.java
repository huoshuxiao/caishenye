package com.sun.caishenye.octopus.job;

import com.sun.caishenye.octopus.fund.service.FundService;
import com.sun.caishenye.octopus.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AgentJob {

    @Autowired
    private FundService fundService;

    @Autowired
    private StockService stockService;

    @Scheduled(cron = "${job.cron.fund}")
    public void fund() {
        LocalDateTime startTime = LocalDateTime.now();
        fundService.run();
        LocalDateTime endTime = LocalDateTime.now();
        log.info("fund job run :: {} minutes", ChronoUnit.MINUTES.between(startTime, endTime));
    }

    @Scheduled(cron = "${job.cron.stock}")
    public void stock() throws InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            stockService.run();
        } catch (ExecutionException e) {
            try {
                TimeUnit.MINUTES.sleep(30);
                stockService.run();
            } catch (ExecutionException executionException) {
                log.error("stock job run error :: {}", e);
            }
        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("stock job run :: {} minutes", ChronoUnit.MINUTES.between(startTime, endTime));
    }
}
