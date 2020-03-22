package com.sun.caishenye.octopus.stock.business.webmagic;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.fund.business.webmagic.TextFilePipeline;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

/**
 * 新浪财经 财务报表 PageProcessor
 * <p>
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 */
@Component
@Slf4j
public class FinancialReportDataPageProcessor implements PageProcessor {

    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100).setTimeOut(Integer.MAX_VALUE);

    // home page
    private final String FILE_PATH = "data";
    private final String FILE_NAME = "FinancialReport.csv";

    @Override
    public void process(Page page) {

        /* 部分二：定义如何抽取页面信息，并保存下来 */
        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
        Html html = page.getHtml();
//        log.debug("html :: {} ", html);

        /* 采集数据 */
        StockDomain stockDomain = dataAgent(html);

        // 保存结果至Pipeline，持久化对象结果
        page.putField(stockDomain.getCompanyCode(), stockDomain.toFrStr());
        log.debug("FinancialReport value :: {}", stockDomain);
    }

    private StockDomain dataAgent(Html html) {
        StockDomain stockDomain = new StockDomain();
        // 公司代码
        stockDomain.setCompanyCode(html.xpath("[@id='toolbar']/div[1]/h2/text()").get());
        // 公司简称
        stockDomain.setCompanyName(html.xpath("[@id='toolbar']/div[1]/h1/a/text()").get());
        // 截止日期
        stockDomain.getFrDomain().setDeadline(html.xpath("[@id=\"FundHoldSharesTable\"]/tbody/tr[1]/td[2]/strong/text()").get());
        // 主营业务收入
        String mbi = Utils.formatNumber2String(html.xpath("[@id=\"FundHoldSharesTable\"]/tbody/tr[9]/td[2]/a/text() | [@id=\"FundHoldSharesTable\"]/tbody/tr[9]/td[2]/text()").get().trim().replace(Constants.FR_YUAN.getString(), ""));
        if (mbi.length() == 1) {
            mbi = "0";
        }
        stockDomain.getFrDomain().setMainBusinessIncome(Utils.formatNumber2String(String.valueOf(Double.valueOf(Utils.formatNumber2String(mbi)) / Constants.FR_10000.getInteger())));
        // 净利润
        String np = html.xpath("[@id=\"FundHoldSharesTable\"]/tbody/tr[11]/td[2]/a/text()").get().replace(Constants.FR_YUAN.getString(), "");
        stockDomain.getFrDomain().setNetProfit(Utils.formatNumber2String(String.valueOf(Double.valueOf(Utils.formatNumber2String(np)) / Constants.FR_10000.getInteger())));
        // 净利润率
        stockDomain.getFrDomain().setNetMargin(Utils.rate(stockDomain.getFrDomain().getNetProfit(), stockDomain.getFrDomain().getMainBusinessIncome()));

        return stockDomain;
    }

    @Override
    public Site getSite() {
        site.setCharset("gb2312");
        return site;
    }

    public void run(List<String> urls) {
        Spider.create(this)
                .startUrls(urls)
                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
                .addPipeline(new TextFilePipeline(FILE_PATH, FILE_NAME))  // 使用Pipeline保存结果到文件
                .thread(Constants.THREADS.getInteger())
                .run();
    }
}
