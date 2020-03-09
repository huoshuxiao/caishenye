package com.sun.caishenye.octopus.fund.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundService {

    private MorningStarService morningStarService;
    private EastMoneyService eastMoneyService;

    @Autowired
    public FundService(MorningStarService morningStarService, EastMoneyService eastMoneyService) {
        this.morningStarService = morningStarService;
        this.eastMoneyService = eastMoneyService;
    }

    public void run() {
        morningStarService.base();
        morningStarService.extend();
        eastMoneyService.detail();
        morningStarService.detail();
    }
}
