package com.sun.caishenye.octopus.fund.controller;

import com.sun.caishenye.octopus.fund.dao.FundDataMongoDao;
import com.sun.caishenye.octopus.fund.domain.FundDataMongoDomain;
import com.sun.caishenye.octopus.fund.service.FundDataSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("search/")
public class FundDataSearchController {

    @Autowired
    private FundDataSearchService fundDataSearchService;

    @GetMapping("funddata")
    public List<FundDataMongoDomain> getFundDataAll(){
        return fundDataSearchService.searchFundData();
    }
}
