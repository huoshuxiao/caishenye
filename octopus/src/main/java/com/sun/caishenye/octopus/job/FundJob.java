package com.sun.caishenye.octopus.job;

import com.sun.caishenye.octopus.fund.service.FundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class FundJob {

    private FundService fundService;
    @Autowired
    public FundJob(FundService fundService) {
        this.fundService = fundService;
    }

    // 每小时一次
    @Scheduled(cron = "${job.cron.fund}")
    public void run() {
        LocalDateTime startTime = LocalDateTime.now();
        fundService.run();
        LocalDateTime endTime = LocalDateTime.now();
        log.info("job run :: {} minutes", ChronoUnit.MINUTES.between(startTime, endTime));
    }
}
