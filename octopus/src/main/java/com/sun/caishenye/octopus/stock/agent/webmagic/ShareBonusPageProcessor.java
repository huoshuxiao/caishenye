package com.sun.caishenye.octopus.stock.agent.webmagic;

import com.sun.caishenye.octopus.common.Constants;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 新浪财经  分红配股 PageProcessor
 * <p>
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 */
@Component
@Slf4j
public class ShareBonusPageProcessor implements PageProcessor {

    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
    protected Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(2000)
            .setTimeOut(Integer.MAX_VALUE)
            .setCycleRetryTimes(3)//这个重试会换IP重试,是setRetryTimes的上一层的重试,不要怕三次重试解决一切问题。。
            .setUseGzip(true)
            .setCharset("gb2312");

    // home page
    protected final String FILE_NAME = Constants.FILE_SHARE_BONUS1.getString();

    protected final String DATA_404 = "暂时没有数据！";

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
            if (stockDomain.getSbDomain().getBonusDate().equals(DATA_404)) {
                continue;
            }
            // 保存结果至Pipeline，持久化对象结果
            page.putField(stockDomain.getSbDomain().getBonusDate() + "@" + stockDomain.getCompanyCode(), stockDomain.sbBuilder1());
            log.debug("ShareBonus value :: {}", stockDomain);
        }
    }

    protected List<StockDomain> dataAgent(Html html) {

        // 每期数据所占tr的行数
        int trSize = html.xpath("[@id='sharebonus_1']/tbody/tr").all().size();
        // 根据数据 创建list size
        List<StockDomain> dataList = new ArrayList<>(trSize);

        // 公司代码
        String companyCode = html.xpath("[@id='toolbar']/div[1]/h2/text()").get().trim();
        // 公司简称
        String companyName = html.xpath("[@id='toolbar']/div[1]/h1/a/text()").get().trim();

        for (int i = 1; i <= trSize; i++) {

            // 公告日期
            String bonusDate = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[1]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();
            // 送股(股)
            String bonus = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[2]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();
            // 转增(股)
            String increase = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[3]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();
            // 派息(税前)(元)
            String dividend = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[4]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();
            // 进度
            String schedule = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[5]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();
            // 除权除息日
            String dividendDate = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[6]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();
            // 股权登记日
            String registrationDate = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[7]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();
            // 红股上市日
            String listingDate = html.xpath("[@id='sharebonus_1']/tbody/tr["+ i +"]/td[8]/text() | [@id='sharebonus_1']/tbody/tr/td/text()").get().trim();

            StockDomain stockDomain = new StockDomain();
            // 公司代码
            stockDomain.setCompanyCode(companyCode);
            // 公司简称
            stockDomain.setCompanyName(companyName);

            // 解决：除权除息日 大于 当前日期时(还未分红)，进度为实施的问题。
            if (Constants.SB_SCHEDULE_IMPLEMENT.getString().equals(schedule)
                    && dividendDate.length() >= 10
                    && LocalDate.now().isBefore(LocalDate.of(Integer.parseInt(dividendDate.substring(0,4)), Integer.parseInt(dividendDate.substring(5,7)), Integer.parseInt(dividendDate.substring(8,10))))) {

                    schedule = Constants.SB_SCHEDULE_PLAN.getString();
            }

            stockDomain.getSbDomain().setBonusDate(bonusDate);
            stockDomain.getSbDomain().setBonus(bonus);
            stockDomain.getSbDomain().setIncrease(increase);
            stockDomain.getSbDomain().setDividend(dividend);
            stockDomain.getSbDomain().setSchedule(schedule);
            stockDomain.getSbDomain().setDividendDate(dividendDate);
            stockDomain.getSbDomain().setRegistrationDate(registrationDate);
            stockDomain.getSbDomain().setListingDate(listingDate);

            dataList.add(stockDomain);
        }

        return dataList;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void run(List<String> urls) {

//        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(
//                new Proxy("1.198.73.36",9999)
//                ,new Proxy("114.230.86.7",9999)));

        Spider.create(this)
                .startUrls(urls)
//                .setDownloader(httpClientDownloader)
//                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
                .addPipeline(new TextFilePipeline(cache.getFilePath(), FILE_NAME))  // 使用Pipeline保存结果到文件
                .thread(Constants.THREADS.getInteger())
                .run();
    }
}
