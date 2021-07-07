//package com.sun.caishenye.octopus.fund.agent.webmagic;
//
//import com.sun.caishenye.octopus.common.Constants;
//import com.sun.caishenye.octopus.fund.domain.MorningStarBaseDomain;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.map.HashedMap;
//import org.apache.commons.lang3.RandomUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//import us.codecraft.webmagic.Page;
//import us.codecraft.webmagic.Request;
//import us.codecraft.webmagic.Site;
//import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.model.HttpRequestBody;
//import us.codecraft.webmagic.pipeline.ConsolePipeline;
//import us.codecraft.webmagic.pipeline.TextFilePipeline;
//import us.codecraft.webmagic.processor.PageProcessor;
//import us.codecraft.webmagic.selector.Html;
//import us.codecraft.webmagic.selector.Selectable;
//import us.codecraft.webmagic.utils.HttpConstant;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Morning Star PageProcessor
// * <p>
// * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
// */
//@Component
//@Slf4j
//public class MorningStarLoginPageProcessor implements PageProcessor {
//
//    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
//    protected Site site = Site.me().setRetryTimes(3).setSleepTime(2000).setTimeOut(Integer.MAX_VALUE);
//
//    // 基金工具->基金筛选器/
//    protected final String URL = "https://morningstar.cn/handler/authentication.ashx";
//
//    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑，页面元素的抽取
//    // 使用了三种抽取技术：XPath、正则表达式和CSS选择器。另外，对于JSON格式的内容，可使用JsonPath进行解析。
//    @Override
//    public void process(Page page) {
//
//        /* 部分二：定义如何抽取页面信息，并保存下来 */
//        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
//        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
//
//        // 登录页面
//        doLogin(page);
//    }
//
//    private void doLogin(Page page) {
//
////        Request request = null;
////        // 使用form表单提交的方式
////        request = setAuthenticationPostRequest();
////        // 将Request加入Scheduler中
////        /* 部分三：从页面发现后续的url地址来抓取 */
////        page.addTargetRequest(request);
//        // 设置skip之后，这个页面的结果不会被Pipeline处理
//        page.setSkip(Boolean.TRUE);
//    }
//
//    private Request setAuthenticationPostRequest() {
//
//        Request request = new Request(URL);
//        request.setMethod(HttpConstant.Method.POST);
//
//        Map<String, Object> params = new HashedMap();
//        params.put("command", "login");
//        params.put("save", "all");
//        params.put("randomid", RandomUtils.nextLong());
////        params.put("username", "bfupst16435@chacuo.net");
//        params.put("password", "bfupst16435");
//
//        String encoding = StandardCharsets.UTF_8.name();
//        HttpRequestBody requestBody = HttpRequestBody.form(params, encoding);
//        request.setRequestBody(requestBody);
//
//
//        return request;
//    }
//
//    @Override
//    public Site getSite() {
//        return site;
//    }
//
//    public void run() {
//
//        Spider.create(new MorningStarLoginPageProcessor())
//                .addUrl("http://cn.morningstar.com/quickrank/default.aspx")   // add url to Scheduler
//                .addRequest(setAuthenticationPostRequest())
////        morningStarSpider
////                .addRequest(setAuthenticationPostRequest())
////                .setScheduler()   // Scheduler包括两个作用： 对待抓取的URL队列进行管理, 对已抓取的URL进行去重。
////                .setDownloader()  // Downloader负责从互联网上下载页面。如：SeleniumDownloader
//                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
////                .addPipeline(new TextFilePipeline(FILE_PATH, FILE_NAME))  // 使用Pipeline保存结果到文件
//                .thread(Constants.THREADS.getInteger())
//                .run();
//    }
//}
