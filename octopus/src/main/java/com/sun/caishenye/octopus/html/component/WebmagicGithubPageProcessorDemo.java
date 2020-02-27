package com.sun.caishenye.octopus.html.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
@Slf4j
public class WebmagicGithubPageProcessorDemo implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑，页面元素的抽取
    // 使用了三种抽取技术：XPath、正则表达式和CSS选择器。另外，对于JSON格式的内容，可使用JsonPath进行解析。
    @Override
    public void process(Page page) {
        /* 正则表达式 */
        // 这段代码的分为两部分，page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all()
        // 用于获取所有满足"(https:/ /github\.com/\w+/\w+)"这个正则表达式的链接，
        // page.addTargetRequests()则将这些链接加入到待抓取的队列中去。
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        String author = page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString();
        // XPath
        String name = page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString();

        page.putField("author", author);
        page.putField("name", name);
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

        log.info(String.format("author >> %s ", author));
        log.info("name >> {} ", name);
        log.info("readme >> {} ", page.getHtml().xpath("//div[@id='readme']/tidyText()").toString());

    }

    @Override
    public Site getSite() {
        return site;
    }

    public void run() {
        Spider.create(new WebmagicGithubPageProcessorDemo()).addUrl("https://github.com/huoshuxiao").thread(5).run();
    }

}
