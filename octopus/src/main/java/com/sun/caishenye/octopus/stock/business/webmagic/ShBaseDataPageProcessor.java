package com.sun.caishenye.octopus.stock.business.webmagic;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.fund.business.webmagic.TextFilePipeline;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 上海证券交易所 基础数据提取器 PageProcessor
 * <p>
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 */
@Component
@Slf4j
public class ShBaseDataPageProcessor implements PageProcessor {

    SeleniumDownloader sDownloader = null;
    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
    protected Site site = Site.me().setRetryTimes(3).setSleepTime(100).setTimeOut(Integer.MAX_VALUE)
            .addHeader("Connection", "keep-alive")
            .addHeader("Cache-Control", "max-age=0")
            .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");

    private final String CHROME_DRIVER_PATH = "/home/sunwenkun/Developer/gitroot/caishenye.git/octopus/bin/chromedriver";

    /* 业务用数据 */
    // 提取数据 目标url (上市公司列表)
    protected final String URL = "http://www.sse.com.cn/assortment/stock/list/share/";
    // 提取数据 保存地址
    protected final String FILE_PATH = "data";
    // 提取数据 保存文件名
    protected final String FILE_NAME = "ShangZhengBase.log";

    @Override
    public void process(Page page) {

        /* 部分二：定义如何抽取页面信息，并保存下来 */
        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
        Html html = page.getHtml();
        log.debug("html :: {} ", html);

        /* 采集数据 */
        List<StockDomain> stockDomainList = dataAgent(html);

        for (StockDomain stockDomain: stockDomainList) {

            // 保存结果至Pipeline，持久化对象结果
            page.putField(stockDomain.getCompanyCode(), stockDomain.toStr());
            log.debug("Shanghai Stock Domain value :: {}", stockDomain);
        }

        //////////////////////////// 当前页面数据采集完毕，构建翻页请求 //////////////////////////////////////
        WebDriver webDriver = sDownloader.getWebDriver();
        WebElement nextNpage = webDriver.findElement(By.cssSelector(".next-page"));
        log.debug("webDriver url :: {}", nextNpage);
        webDriver.findElement(By.cssSelector(".next-page")).click();
    }

    protected List<StockDomain> dataAgent(Html html) {

        // 根据每页数量定义List大小
        List<String> trs = html.css(".js_tableT01").css(".sse_table_T01").xpath("tr").all();
        int size = trs.size() - 1;
        List<StockDomain> domainList = new ArrayList<>(size);

        // i从1开始，0为header，1为body数据
        for (int i = 1; i < trs.size(); i++) {
            log.debug("tr :: {}", trs.get(i));
            // 为了让Html.create构建出html元素，补充table元素
            String htmlTableTemplate = "<table>{0}</table>";
            Html trHtml = Html.create(htmlTableTemplate.replace("{0}", trs.get(i)));
            List<String> tds = trHtml.xpath("td").all();

            // 公司代码
            String companyCode = "";
            // 公司简称
            String companyName = "";
            // 上市日期
            String listingDate = "";
            // 证券交易所
            String exchange = Constants.EXCHANGE_SH.getString();

            for (int j = 2; j < tds.size() - 1; j++) {
                log.debug("td :: {}", tds.get(j));
                // 为了让Html.create构建出html元素，补充table元素
                String htmlTableTrTemplate = "<table><tr>{0}</tr></table>";
                Html tdHtml = Html.create(htmlTableTrTemplate.replace("{0}", tds.get(j)));
                // 取得数据
                String tdValue = tdHtml.xpath("td/a/text() | td/text()").get();
                log.debug("td value :: {}", tdValue);

                switch (j) {
                    case 2:
                        companyCode = tdValue;
                        break;
                    case 3:
                        companyName = tdValue;
                        break;
                    case 4:
                        listingDate = tdValue;
                        break;
                }
            }

            // 保存采集对象结果
            StockDomain stockDomain = new StockDomain();
            stockDomain.setExchange(exchange);
            stockDomain.setCompanyCode(companyCode);
            stockDomain.setCompanyName(companyName);
            stockDomain.setListingDate(listingDate);

            domainList.add(stockDomain);
        }
        return domainList;
    }

    @Override
    public Site getSite() {
        site.setCharset(StandardCharsets.UTF_8.name());
        return site;
    }

    public void run() {
        sDownloader = new SeleniumDownloader(CHROME_DRIVER_PATH).setSleepTime(2000);
        Spider.create(this)
                .addUrl(URL)   // add url to Scheduler
                .setDownloader(sDownloader)
                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
                .addPipeline(new TextFilePipeline(FILE_PATH, FILE_NAME))  // 使用Pipeline保存结果到文件
                .thread(Constants.THREADS.getInteger())
                .run();
    }
}
