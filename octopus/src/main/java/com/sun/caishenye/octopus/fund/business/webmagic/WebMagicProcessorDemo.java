package com.sun.caishenye.octopus.fund.business.webmagic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
@Slf4j
public class WebMagicProcessorDemo implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(5000);

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑，页面元素的抽取
    // 使用了三种抽取技术：XPath、正则表达式和CSS选择器。另外，对于JSON格式的内容，可使用JsonPath进行解析。
    @Override
    public void process(Page page) {
        /* 正则表达式 */
        // 这段代码的分为两部分，page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all()
        // 用于获取所有满足"(https:/ /github\.com/\w+/\w+)"这个正则表达式的链接，
        // page.addTargetRequests()则将这些链接加入到待抓取的队列中去。
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());

        String author = page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString();
        // XPath
        String name = page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString();

        // 保存结果，这个结果会最终保存到ResultItems中(KV的形式按行输出)
        page.putField("author", author);
        page.putField("name", name);
        if (page.getResultItems().get("name")==null){
            // 设置skip之后，这个页面的结果不会被Pipeline处理
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

        log.info(String.format("author >> %s ", author));
        log.info("name >> {} ", name);
        log.info("readme >> {} ", page.getHtml().xpath("//div[@id='readme']/tidyText()").toString());

        page.addTargetRequest("https://github.com/huoshuxiao/caishenye");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void run() {
        Spider demo = Spider.create(new WebMagicProcessorDemo()).addUrl("https://github.com/huoshuxiao");

        demo
//            .setScheduler()   // Scheduler包括两个作用： 对待抓取的URL队列进行管理, 对已抓取的URL进行去重。
//            .setDownloader()  // Downloader负责从互联网上下载页面。如：SeleniumDownloader
            .addPipeline(new ConsolePipeline()) // 输出结果到控制台
            .addPipeline(new FilePipeline("MorningStarDEMO"))  // 使用Pipeline保存结果到文件
            .thread(5)
            .run();
    }

}
