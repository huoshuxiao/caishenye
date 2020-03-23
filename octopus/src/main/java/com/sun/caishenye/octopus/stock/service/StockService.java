package com.sun.caishenye.octopus.stock.service;


import com.sun.caishenye.octopus.stock.business.webmagic.FinancialReportDataPageProcessor;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 证券 总服务
 */
@Slf4j
@Service
public class StockService {

    // 财务报表:财务摘要 —新浪财经
    protected final String FR_BASE_URL =
//            "https://money.finance.sina.com.cn/corp/go.php/vFD_FinanceSummary/stockid/{companyCode}/displaytype/4.phtml";
            "https://money.finance.sina.com.cn/corp/go.php/vFD_FinanceSummary/stockid/{companyCode}.phtml";

    @Autowired
    private ShService shService;

    @Autowired
    private SzService szService;

    @Autowired
    private FinancialReportDataPageProcessor frPageProcessor;

    // 财务报表
    public Object financialReport() {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = readBaseData();

        List<String> urls = new ArrayList<>(stockDomainList.size());
        for (StockDomain stockDomain: stockDomainList) {
            urls.add(FR_BASE_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
        }

        // 新浪财经 财务报表
        frPageProcessor.run(urls);

        return "finished";
    }

    // 查询证券基础数据
    public List<StockDomain> readBaseData() {
        List<StockDomain> shStockDomainList = shService.readBaseData();
        List<StockDomain> szStockDomainList = szService.readBaseData();
        return Stream.concat(shStockDomainList.stream(), szStockDomainList.stream()).collect(Collectors.toList());
    }
}
