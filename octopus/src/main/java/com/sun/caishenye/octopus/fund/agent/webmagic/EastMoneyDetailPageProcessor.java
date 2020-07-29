package com.sun.caishenye.octopus.fund.agent.webmagic;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.TextFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.SelectableUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Morning Star PageProcessor
 * <p>
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 */
@Component
@Slf4j
public class EastMoneyDetailPageProcessor implements PageProcessor {

    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
    protected Site site = Site.me().setRetryTimes(3).setSleepTime(2000).setTimeOut(Integer.MAX_VALUE);

    // home page
    protected final String FILE_PATH = "data";
    protected final String FILE_NAME = Constants.FILE_EAST_MONEY_DETAIL.getString();
    private final String RISK_MAEESAGE = "友情提示：该基金可能由于巨额赎回等原因，基金净值和阶段涨幅出现异常波动。";

    @Override
    public void process(Page page) {

        /* 部分二：定义如何抽取页面信息，并保存下来 */
        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
        Html html = page.getHtml();

        /* 采集数据 */
        EastMoneyDetailDomain eastMoneyDetailDomain = dataAgent(html);

        if (StringUtils.isEmpty(eastMoneyDetailDomain.getFundCode()) || StringUtils.isEmpty(eastMoneyDetailDomain.getFundName())) {
            log.debug("url not found >> {}", page.getUrl().get());
            page.setSkip(Boolean.TRUE);
        }

        // 保存结果至Pipeline，持久化对象结果
        page.putField(eastMoneyDetailDomain.getFundCode() + "@" + eastMoneyDetailDomain.getFundName(), eastMoneyDetailDomain.toStr());
        log.debug("EastMoneyDetailDomain value :: {}", eastMoneyDetailDomain.toString());
    }

    protected EastMoneyDetailDomain dataAgent(Html html) {
        EastMoneyDetailDomain eastMoneyDetailDomain = new EastMoneyDetailDomain();
        // 友情提示 风险
        if (RISK_MAEESAGE.equals(html.css(".xfinfo").xpath("span/text()").get())) {
            eastMoneyDetailDomain.setRisk(Boolean.TRUE);
        }

        Selectable merchandiseDetail = html.css(".wrapper")
                .css(".wrapper_min")
                .css(".merchandiseDetail");
        // 基金名称
        String fundName = merchandiseDetail
                .css(".fundDetail-header")
                .css(".fundDetail-tit")
                .xpath("div/div/text()")
                .get();
        eastMoneyDetailDomain.setFundName(fundName);

        // 基金代码
        String fundCode = merchandiseDetail
                .css(".fundDetail-header")
                .css(".fundDetail-tit")
                .css(".ui-num")
                .xpath("span/text()")
                .get();
        eastMoneyDetailDomain.setFundCode(fundCode);

        Selectable dataItem02 = merchandiseDetail.css(".fundDetail-main")
                .css(".fundInfoItem")
                .css(".dataOfFund")
                .css(".dataItem02")
                ;

        // 净值日期
        eastMoneyDetailDomain.setClosePriceDate(SelectableUtils.getValue(dataItem02.xpath("dt/p/text()")).replace("(","").replace(")",""));
        // 单位净值
        String closePrice = SelectableUtils.getValue(dataItem02.xpath("dd/span/text()"));
        // 货币基金 7日年华收益率 ，排除此类数据
        if (closePrice.contains("%")) {
            closePrice = "-";
        }
        eastMoneyDetailDomain.setClosePrice(closePrice);
        // 基金类型
        eastMoneyDetailDomain.setType(SelectableUtils.getValue(html.xpath("[@id='body']/div[12]/div/div/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[1]/a/text()")));
        if ("-".equals(eastMoneyDetailDomain.getType())) {
            eastMoneyDetailDomain.setType(SelectableUtils.getValue(html.xpath("[@id='body']/div[12]/div/div/div[3]/div[1]/div[2]/table/tbody/tr[1]/td[1]/a/text()")));
        }
        if ("-".equals(eastMoneyDetailDomain.getType())) {
            eastMoneyDetailDomain.setType(SelectableUtils.getValue(html.xpath("[@id='body']/div[13]/div/div/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[1]/a/text()")));
        }
        if ("-".equals(eastMoneyDetailDomain.getType())) {
            eastMoneyDetailDomain.setType(SelectableUtils.getValue(html.xpath("[@id='body']/div[13]/div/div/div[3]/div[1]/div[2]/table/tbody/tr[1]/td[1]/a/text()")));
        }
        if ("-".equals(eastMoneyDetailDomain.getType())) {
            eastMoneyDetailDomain.setType(SelectableUtils.getValue(html.xpath("[@id='body']/div[4]/div[9]/div/div/div[2]/div[1]/div[3]/table/tbody/tr[1]/td[1]/a/text()")));
        }
        // 基金规模
        eastMoneyDetailDomain.setSize(SelectableUtils.getValue(html.xpath("[@id='body']/div[12]/div/div/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[2]/text()")));
        if ("-".equals(eastMoneyDetailDomain.getSize())) {
            eastMoneyDetailDomain.setSize(SelectableUtils.getValue(html.xpath("[@id='body']/div[12]/div/div/div[3]/div[1]/div[2]/table/tbody/tr[1]/td[2]/text()")));
        }
        if ("-".equals(eastMoneyDetailDomain.getSize())) {
            eastMoneyDetailDomain.setSize(SelectableUtils.getValue(html.xpath("[@id='body']/div[13]/div/div/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[2]/text()")));
        }
        if ("-".equals(eastMoneyDetailDomain.getSize())) {
            eastMoneyDetailDomain.setSize(SelectableUtils.getValue(html.xpath("[@id='body']/div[13]/div/div/div[3]/div[1]/div[2]/table/tbody/tr[1]/td[2]/text()")));
        }
        if ("-".equals(eastMoneyDetailDomain.getSize())) {
            eastMoneyDetailDomain.setSize(SelectableUtils.getValue(html.xpath("[@id='body']/div[4]/div[9]/div/div/div[2]/div[1]/div[3]/table/tbody/tr[1]/td[2]/text()")));
        }
        eastMoneyDetailDomain.setSize(eastMoneyDetailDomain.getSize().replace("：", ""));
        return eastMoneyDetailDomain;
    }

    @Override
    public Site getSite() {
        site.setCharset(StandardCharsets.UTF_8.name());
        return site;
    }

    public void run(List<String> urls) {
        Spider.create(new EastMoneyDetailPageProcessor())
                .startUrls(urls)
//                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
                .addPipeline(new TextFilePipeline(FILE_PATH, FILE_NAME))  // 使用Pipeline保存结果到文件
                .thread(Constants.THREADS.getInteger())
                .run();
    }
}
