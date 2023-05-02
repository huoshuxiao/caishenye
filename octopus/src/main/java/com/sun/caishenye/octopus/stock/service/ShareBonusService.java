package com.sun.caishenye.octopus.stock.service;

import com.google.gson.Gson;
import com.sun.caishenye.octopus.stock.agent.api.ApiRestTemplate;
import com.sun.caishenye.octopus.stock.agent.webmagic.ShareBonusPageProcessor;
import com.sun.caishenye.octopus.stock.dao.StockDao;
import com.sun.caishenye.octopus.stock.domain.ShareBonusDomain;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 分红
 */
@Slf4j
@Service
public class ShareBonusService {

    // 发行与分配:分红配股 — 新浪财经
    // https://money.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/603005.phtml
    private static final String SB_BASE_URL = "https://money.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/{companyCode}.phtml";

    @Autowired
    private BaseService baseService;

    @Autowired
    private ShareBonusPageProcessor sbPageProcessor;

    @Autowired
    private StockDao stockDao;

    @Autowired
    private ApiRestTemplate apiRestTemplate;

    // 分红配股
    public Object shareBonus() {

        // 查询证券基础数据
        List<StockDomain> baseList = baseService.readBaseData();

        List<String> urls = new ArrayList<>(baseList.size());
        for (StockDomain stockDomain: baseList) {
            urls.add(SB_BASE_URL.replace("{companyCode}", stockDomain.getCompanyCode()));
        }
        // 新浪财经 分红配股
        sbPageProcessor.run(urls);

        // 雪球 分红配股
        List<StockDomain> xueqiuSBList = new ArrayList<>();
        Gson gson = new Gson();
        for (StockDomain base : baseList) {
            String jsonString = gson.toJson(base);
            List<ShareBonusDomain> sbList = apiRestTemplate.getXueqiuShareBonus(base.getCompanyCode(), base.getExchange());
            sbList.sort(Comparator.comparing(ShareBonusDomain::getDividendYear).reversed());
            for (ShareBonusDomain sb: sbList) {
                StockDomain clone = gson.fromJson(jsonString, StockDomain.class);
                clone.setSbDomain(sb);
                xueqiuSBList.add(clone);
            }
        }

        // merge
        List<StockDomain> mergedSBList = stockDao.readShareBonus1();
        mergedSBList.forEach(t -> {
            for (StockDomain t2 : xueqiuSBList) {
                if (t.getCompanyCode().equals(t2.getCompanyCode()) && t.getSbDomain().getDividendDate().equals(t2.getSbDomain().getDividendDate())) {
                    t.getSbDomain().setDividendYear(t2.getSbDomain().getDividendYear());
                    break;
                }
            }
        });

        stockDao.writeShareBonus(mergedSBList);
        return "finished";
    }

    public List<StockDomain> readShareBonus() {
        return stockDao.readShareBonus();
    }
}
