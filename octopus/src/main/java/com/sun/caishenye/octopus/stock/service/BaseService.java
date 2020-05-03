package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基础数据
 */
@Slf4j
@Service
public class BaseService {

    @Autowired
    private ShService shService;

    @Autowired
    private SzService szService;

    // 查询证券基础数据
    public List<StockDomain> readBaseData() {
        List<StockDomain> shStockDomainList = shService.readBaseData();
        List<StockDomain> szStockDomainList = szService.readBaseData();
        return Stream.concat(shStockDomainList.stream(), szStockDomainList.stream()).collect(Collectors.toList());
    }
}
