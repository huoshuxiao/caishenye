package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.agent.webmagic.ShareBonusPageProcessor;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 分红
 */
@Slf4j
@Service
public class ShareBonusService {

    // 发行与分配:分红配股 — 新浪财经
    private static final String SB_BASE_URL = "https://money.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/{companyCode}.phtml";

    @Autowired
    private BaseService baseService;

    @Autowired
    private ShareBonusPageProcessor sbPageProcessor;

    @Autowired
    private StockDao stockDao;

    // 分红配股
    public Object shareBonus() {

        // 查询证券基础数据
        List<StockDomain> stockDomainList = baseService.readBaseData();

        List<String> urls = new ArrayList<>(stockDomainList.size());
        for (StockDomain stockDomain: stockDomainList) {
            urls.add(SB_BASE_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
        }

        // 新浪财经 分红配股
        sbPageProcessor.run(urls);
        return "finished";
    }

    public List<StockDomain> readShareBonus() {
        return stockDao.readShareBonus();
    }
}
