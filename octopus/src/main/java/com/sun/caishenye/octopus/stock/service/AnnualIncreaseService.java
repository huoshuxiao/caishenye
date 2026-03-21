package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.AnnualIncreaseDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 年度涨跌幅
 */
@Slf4j
@Service
public class AnnualIncreaseService {

    @Autowired
    private BaseService baseService;

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    @Autowired
    private StockDao stockDao;

    @Value("${years}")
    private int years;

    public Object execute() {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = baseService.readBaseData();
        List<AnnualIncreaseDomain> result = new ArrayList<>();
        for (StockDomain stockDomain: stockDomainList) {
            // 采集 历史行情
            List<AnnualIncreaseDomain> data = getHhqByDateForObject(stockDomain);
            result.addAll(data);
        }
        // 写入 历史行情 数据
        writeAnnualIncreaseData(result);

        return "AnnualIncrease";
    }

    // 历史行情(指定日期)
    private List<AnnualIncreaseDomain> getHhqByDateForObject(StockDomain stockDomain) {
        List<AnnualIncreaseDomain> result = new ArrayList<>();
        try {
            int endYear = LocalDate.now().getYear();
            int startYear = endYear - years;
            for (int year = startYear; year <= endYear; year++) {
                String s_date = String.format("%d0101", year);
                String e_date = String.format("%d1231", year);
                result.add(apiRestTemplate.getHhqByDateForObject(stockDomain, s_date, e_date));
                Thread.sleep(150);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // 写 历史行情
    public void writeAnnualIncreaseData(List<AnnualIncreaseDomain> data) {
        stockDao.writeAnnualIncreaseData(data);
    }
    // 读 历史行情
    public List<AnnualIncreaseDomain> readAnnualIncreaseData() {
        return stockDao.readAnnualIncreaseData();
    }
}
