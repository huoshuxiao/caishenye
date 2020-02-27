package com.sun.caishenye.octopus.html.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * PageProcessor
 *
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 *
 */
@Component
@Slf4j
public class MorningStarPageProcessor implements PageProcessor {

    // 基金工具->基金筛选器/
    private final String URL = "http://cn.morningstar.com/quickrank/default.aspx";

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(5000);

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑，页面元素的抽取
    // 使用了三种抽取技术：XPath、正则表达式和CSS选择器。另外，对于JSON格式的内容，可使用JsonPath进行解析。
    @Override
    public void process(Page page) {

        /* 部分二：定义如何抽取页面信息，并保存下来 */
        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
        Html html = page.getHtml();

        /* 获取抽取页面 */
        // active tab check
        Selectable activeTabSelectable = html.xpath("div[@id='qr_tabcommand']/div[@id='qr_tab']/a[@class='active']");
        String activeTabId = activeTabSelectable.xpath("a/@id").get();
        log.debug("active tab id is {} ", activeTabId);

        // 快照 页面
        // <a id="ctl00_cphMain_lbSnapshot" class="active" href="javascript:__doPostBack('ctl00$cphMain$lbSnapshot','')">快照</a>
        if ("ctl00_cphMain_lbSnapshot".equals(activeTabId)) {
            log.debug("ctl00_cphMain_lbSnapshot");
            // -> 跳转到 业绩和风险tab页(ctl00_cphMain_lbPerformance)
            String performanceTabHref =  html.xpath("div[@id='qr_tabcommand']/div[@id='qr_tab']/a[@id='ctl00_cphMain_lbPerformance']/@href").get();
            Request request = setFormPostRequest(performanceTabHref, html);
            // 使用form表单提交的方式
            page.addTargetRequest(request);

        // 业绩和风险页面
        // <a id="ctl00_cphMain_lbPerformance" class="active" href="javascript:__doPostBack('ctl00$cphMain$lbPerformance','')">业绩和风险</a>
        } else if ("ctl00_cphMain_lbPerformance".equals(activeTabId)) {
            log.debug("ctl00_cphMain_lbPerformance");
            // 采集 业绩和风险 数据

            // 翻页
//            Request request = setFormPostRequest(activeTabHref, html);
//            // 使用form表单提交的方式
//            page.addTargetRequest(request);
        } else {
            page.setSkip(Boolean.TRUE);
        }
    }

    /**
     * 使用form表单提交的方式
     *
     * @param activeTabHref format: href="javascript:__doPostBack('ctl00$cphMain$lbPerformance','')"
     * @param html
     * @return
     */
    private Request setFormPostRequest(String activeTabHref, Html html) {

        String formParam = StringUtils.substringBetween(activeTabHref,"(",")");
        String[] formParams = formParam.split(",");
        Map<String,Object> params = new HashedMap();
        params.put("__EVENTTARGET", formParams[0].replaceAll("[']",""));
        params.put("__EVENTARGUMENT", formParams[1].replaceAll("[']",""));
        params.put("__LASTFOCUS", html.xpath("form[@id='aspnetForm']/input[@id=__LASTFOCUS]/@value").get());
        params.put("__VIEWSTATE", html.xpath("form[@id='aspnetForm']/input[@id=__VIEWSTATE]/@value").get());
        params.put("__VIEWSTATEGENERATOR", html.xpath("form[@id='aspnetForm']/input[@id=__VIEWSTATEGENERATOR]/@value").get());
        params.put("__EVENTVALIDATION", html.xpath("form[@id='aspnetForm']/input[@id=__EVENTVALIDATION]/@value").get());

        params.put("ctl00$cphMain$ddlCompany", html.xpath("select[@name='ctl00$cphMain$ddlCompany']/option[@selected='selected']/@value").get());
        params.put("ctl00$cphMain$ddlPortfolio", html.xpath("select[@name='ctl00$cphMain$ddlPortfolio']/option[@selected='selected']/@value").get());
        params.put("ctl00$cphMain$ddlWatchList", html.xpath("select[@name='ctl00$cphMain$ddlWatchList']/option[@selected='selected']/@value").get());
        params.put("ctl00$cphMain$txtFund", html.xpath("input[@name=ctl00$cphMain$txtFund]/@value").get());
        params.put("ctl00$cphMain$ddlPageSite", html.xpath("select[@name='ctl00$cphMain$ddlPageSite']/option[@selected='selected']/@value").get());
        String encoding = StandardCharsets.UTF_8.name();
        HttpRequestBody requestBody = HttpRequestBody.form(params, encoding);

        Request request = new Request(URL);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(requestBody);

        return request;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void run() throws Exception {

        Spider morningStarSpider = Spider.create(new MorningStarPageProcessor())
                .addUrl(URL);

        // Monitor for JMX
//        SpiderMonitor.instance().register(morningStarSpider);

        morningStarSpider
//                .addPipeline(new FilePipeline("MorningStar"))  // 使用Pipeline保存结果
                .thread(5)
                .run();
    }
}
