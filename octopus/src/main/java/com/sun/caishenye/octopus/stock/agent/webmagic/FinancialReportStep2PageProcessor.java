package com.sun.caishenye.octopus.stock.agent.webmagic;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.common.component.CacheComponent;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.TextFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.SelectableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 新浪财经 财务报表 PageProcessor
 * <p>
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 */
@Component
@Slf4j
public class FinancialReportStep2PageProcessor implements PageProcessor {

    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
    protected Site site = Site.me().setRetryTimes(3).setSleepTime(100).setTimeOut(Integer.MAX_VALUE).setCharset("gb2312");

    // home page
    protected final String FILE_NAME = Constants.FILE_FINANCIAL_REPORT_STEP2.getString();

    @Autowired
    private CacheComponent cache;

    @Override
    public void process(Page page) {

        /* 部分二：定义如何抽取页面信息，并保存下来 */
        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
        Html html = page.getHtml();
//        log.debug("html :: {} ", html);

        /* 采集数据 */
        List<StockDomain> dataList = dataAgent(html);
        for (StockDomain stockDomain : dataList) {
            // 保存结果至Pipeline，持久化对象结果
            page.putField(stockDomain.getFrDomain().getDeadline() + "@" + stockDomain.getCompanyCode(), stockDomain.frStep2Builder());
            log.debug("FinancialReportStep2DataPageProcessor value :: {}", stockDomain);
        }
    }

    protected List<StockDomain> dataAgent(Html html) {

        // 每期财报所占td数
        int tdSize = html.xpath("[@id='BalanceSheetNewTable0']/tbody/tr[1]/td").all().size();
        // 根据财报期数创建list size
        List<StockDomain> dataList = new ArrayList<>(tdSize);

        String companyCode = html.xpath("[@id='toolbar']/div[1]/h2/text()").get();
        String companyName = html.xpath("[@id='toolbar']/div[1]/h1/a/text()").get();

        for (int i = 2; i <= tdSize; i++) {

            StockDomain stockDomain = new StockDomain();
            // 公司代码
            stockDomain.setCompanyCode(companyCode);
            // 公司简称
            stockDomain.setCompanyName(companyName);

            // 截止日期
            //*[@id="BalanceSheetNewTable0"]/tbody/tr[1]/td[2]
            stockDomain.getFrDomain().setDeadline(SelectableUtils.getValue(html.xpath("[@id='BalanceSheetNewTable0']/tbody/tr[1]/td["+ i +"]/text()")));

            /* 成长能力 */

            // 主营业务收入增长率(%)
            //*[@id="BalanceSheetNewTable0"]/tbody/tr[35]/td[2]
            stockDomain.getFrDomain().setMainBusinessIncomeGrowthRate(Utils.formatNumber2String(SelectableUtils.getValue(html.xpath("[@id='BalanceSheetNewTable0']/tbody/tr[35]/td["+ i +"]/text()"))));

            // 净利润增长率(%)
            stockDomain.getFrDomain().setNetAssetGrowthRate(Utils.formatNumber2String(SelectableUtils.getValue(html.xpath("[@id='BalanceSheetNewTable0']/tbody/tr[36]/td["+ i +"]/text()"))));

            // 净资产增长率(%)
            stockDomain.getFrDomain().setNetProfitGrowthRate(Utils.formatNumber2String(SelectableUtils.getValue(html.xpath("[@id='BalanceSheetNewTable0']/tbody/tr[37]/td["+ i +"]/text()"))));

            // 总资产增长率(%)
            //*[@id="BalanceSheetNewTable0"]/tbody/tr[38]/td[2]
            stockDomain.getFrDomain().setTotalAssetsGrowthRate(Utils.formatNumber2String(SelectableUtils.getValue(html.xpath("[@id='BalanceSheetNewTable0']/tbody/tr[38]/td["+ i +"]/text()"))));

            dataList.add(stockDomain);
        }
        return dataList;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void run(List<String> urls) {
        Spider.create(this)
                .startUrls(urls)
//                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
                .addPipeline(new TextFilePipeline(cache.putIfAbsentFilePath(), FILE_NAME))  // 使用Pipeline保存结果到文件
                .thread(Constants.THREADS.getInteger())
                .run();
    }
}
