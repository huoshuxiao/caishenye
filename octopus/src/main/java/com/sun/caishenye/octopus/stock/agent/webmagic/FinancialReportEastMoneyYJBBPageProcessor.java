package com.sun.caishenye.octopus.stock.agent.webmagic;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.domain.FinancialReport2Domain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.TextFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.SelectableUtils;

import java.util.ArrayList;
import java.util.List;

// TODO 前后端分离，数据爬取失败
/**
 * 东方财富网 财务报表 PageProcessor
 * <p>
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 */
@Component
@Slf4j
public class FinancialReportEastMoneyYJBBPageProcessor implements PageProcessor {

    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
    protected Site site = Site.me().setRetryTimes(1).setSleepTime(5000).setTimeOut(Integer.MAX_VALUE).setCharset("gb2312")
            .addHeader("Connection", "keep-alive")
            .addHeader("Content-Type", "text/html; charset=gb2312")
            .addHeader("Cache-Control", "max-age=0")
            .addHeader("Referer", "http://data.eastmoney.com/")
            .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
            ;

    // home page
    protected final String FILE_PATH = Constants.FILE_PATH.getString();
    protected final String FILE_NAME = Constants.FILE_FINANCIAL_REPORT_EASTMONEY.getString();

    @Override
    public void process(Page page) {

        /* 部分二：定义如何抽取页面信息，并保存下来 */
        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
        Html html = page.getHtml();
        log.debug("html :: {} ", html);

        /* 采集数据 */
        List<FinancialReport2Domain> dataList = dataAgent(html);
        for (FinancialReport2Domain financialDomain : dataList) {
            // 保存结果至Pipeline，持久化对象结果
            page.putField(financialDomain.getDeadline() + "@" + financialDomain.getCompanyCode(), financialDomain.builder());
            log.debug("FinancialReport value :: {}", financialDomain);
        }
    }

    // 按行采集
    protected List<FinancialReport2Domain> dataAgent(Html html) {

        //*[@id="dt_1"]/tbody/tr[1]
        int trSize = html.xpath("[@id='dt_1']/tbody/tr").all().size();

        String company = SelectableUtils.getValue(html.xpath("[@id='page']/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div[1]/a/text()"));
        String companyCode = StringUtils.substringBetween(company, "(", ")");
        String companyName = StringUtils.substringBefore(company, "(");
        //*[@id='newPrice']/span
        String price = SelectableUtils.getValue(html.xpath("[@id='newPrice']/span/text()"));

        List<FinancialReport2Domain> dataList = new ArrayList<>(trSize);
        for (int i = 1; i <= trSize; i++) {
            FinancialReport2Domain financialDomain = new FinancialReport2Domain();

            financialDomain.setCompanyCode(companyCode);
            financialDomain.setCompanyName(companyName);
            financialDomain.setPrice(price);

            // 报告期
            financialDomain.setDeadline(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[1]/span/@title")));
            // 每股收益(元)
            financialDomain.setBasicEps(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[2]/text()")));
            // 每股收益(扣除)(元)
            financialDomain.setCutBasicEps(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[3]/text()")));
            // 营业收入(元)
            financialDomain.setMainBusinessIncome(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[4]/span/text()")));
            // 同营业收入 同比增长(%)
            financialDomain.setMainBusinessIncomeGrowthRate(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[5]/span/text()")));
            // 营业收入 季度环比增长(%)
            financialDomain.setMainBusinessIncomeGrowthRateMoM(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[6]/span/text()")));
            // 净利润(元)
            financialDomain.setNetProfit(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[7]/span/text()")));
            // 净利润 同比增长(%)
            financialDomain.setNetProfitGrowthRate(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[8]/span/text()")));
            // 净利润 季度环比增长(%)
            financialDomain.setNetProfitGrowthRateMoM(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[9]/span/text()")));
            // 每股净资产(元)
            financialDomain.setBps(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[10]/span/text()")));
            // 净资产收益率(%)
            financialDomain.setRoeWeighted(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[11]/span/text()")));
            // 每股经营现金流量(元)
            financialDomain.setPerShareCashFlowFromOperations(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[12]/span/text()")));
            // 销售毛利率(%)
            financialDomain.setGrossProfitMargin(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[13]/span/text()")));
            // 利润分配
            financialDomain.setProfitDistribution(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[14]/text()")));
            // 股息率(%)
            financialDomain.setDividendYield(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[15]/span/text()")));
            // 首次公告日期
            financialDomain.setFirstNoticeDate(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[16]/span/@title")));
            // 最新公告日期
            financialDomain.setLatestNoticeDate(SelectableUtils.getValue(html.xpath("[@id='dt_1']/tbody/tr["+ i +"]/td[17]/span/@title")));
            // 净利润率(净利润/主营业务收入)
//            financialDomain.setNetMargin("-");
            financialDomain.setNetMargin(Utils.rate(financialDomain.getNetProfit(), financialDomain.getMainBusinessIncome()));

            dataList.add(financialDomain);
        }

        return dataList;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void run(List<String> urls) {

        // 设置延时(waiting HTML call API render)
//        SeleniumDownloader downloader = new SeleniumDownloader(Constants.CHROME_DRIVER_PATH.getString()).setSleepTime(2000);

        Spider.create(this)
                .startUrls(urls)
//                .setDownloader(downloader)
//                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
                .addPipeline(new TextFilePipeline(FILE_PATH, FILE_NAME))  // 使用Pipeline保存结果到文件
                .thread(Constants.THREADS.getInteger())
                .run();
    }
}
