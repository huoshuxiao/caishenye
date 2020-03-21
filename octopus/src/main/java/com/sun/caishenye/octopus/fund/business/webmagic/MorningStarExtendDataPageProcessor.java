package com.sun.caishenye.octopus.fund.business.webmagic;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.fund.domain.MorningStarExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Morning Star PageProcessor
 * <p>
 * 分为三个部分，分别是爬虫的配置、页面元素的抽取和链接的发现
 */
@Component
@Slf4j
public class MorningStarExtendDataPageProcessor implements PageProcessor {

    /* 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等 */
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100).setTimeOut(Integer.MAX_VALUE);

    // 基金工具->基金筛选器/
    private final String URL = "http://cn.morningstar.com/quickrank/default.aspx";
    // 快照页面 ID
    private final String EL_ID_CTL00_CPHMAIN_LBSNAPSHOT = "ctl00_cphMain_lbSnapshot";
    // 业绩和风险页面 ID
    private final String EL_ID_CTL00_CPHMAIN_LBPERFORMANCE = "ctl00_cphMain_lbPerformance";

    private AtomicInteger aiNavPageIndex = new AtomicInteger(1);

    private final String FILE_PATH = "data";
    private final String FILE_NAME = "MorningStarExtend.log";

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑，页面元素的抽取
    // 使用了三种抽取技术：XPath、正则表达式和CSS选择器。另外，对于JSON格式的内容，可使用JsonPath进行解析。
    @Override
    public void process(Page page) {

        Request request = null;
        /* 部分二：定义如何抽取页面信息，并保存下来 */
        // Selectable相关的抽取元素链式API是WebMagic的一个核心功能。抽取是支持链式调用的
        // 使用Selectable抽取元素，分为两类：抽取部分和获取结果部分。
        Html html = page.getHtml();

        /* 获取抽取页面 */
        // active tab check
        Selectable activeTabSelectable = html.xpath("div[@id='qr_tabcommand']/div[@id='qr_tab']/a[@class='active']");
        String activeTabId = activeTabSelectable.xpath("a/@id").get();
        log.debug("active tab id is {} ", activeTabId);
        // 翻页阀值，最大值
        int navPageMaxValue = (int) Math.ceil(Double.valueOf(html.xpath("div[@id='qr_pager']/*/span[@id='ctl00_cphMain_TotalResultLabel']/text()").get())
                / Double.valueOf(html.xpath("select[@name='ctl00$cphMain$ddlPageSite']/option[@selected='selected']/@value").get()));

        // 快照 页面
        // <a id="ctl00_cphMain_lbSnapshot" class="active" href="javascript:__doPostBack('ctl00$cphMain$lbSnapshot','')">快照</a>
        if (EL_ID_CTL00_CPHMAIN_LBSNAPSHOT.equals(activeTabId)) {
            log.debug(activeTabId);
            // -> 跳转到 业绩和风险tab页(ctl00_cphMain_lbPerformance)
            String performanceTabHref = html.xpath("div[@id='qr_tabcommand']/div[@id='qr_tab']/a[@id='ctl00_cphMain_lbPerformance']/@href").get();
            log.debug("link el :: {}", performanceTabHref);

            // 使用form表单提交的方式
            request = setFormPostRequest(performanceTabHref, html);
            // 将Request加入Scheduler中
            /* 部分三：从页面发现后续的url地址来抓取 */
            page.addTargetRequest(request);

            // 业绩和风险页面
            // <a id="ctl00_cphMain_lbPerformance" class="active" href="javascript:__doPostBack('ctl00$cphMain$lbPerformance','')">业绩和风险</a>
        } else if (EL_ID_CTL00_CPHMAIN_LBPERFORMANCE.equals(activeTabId) && aiNavPageIndex.get() <= navPageMaxValue) {

            log.debug(activeTabId);
            /* 采集数据 */
            List<MorningStarExtendDomain> morningStarDTOList = dataAgent(html);

            // 保存结果至Pipeline，持久化对象结果
            for (MorningStarExtendDomain morningStarDTO : morningStarDTOList) {
                // fundCode会为空，做组合key
                page.putField(morningStarDTO.getFundId() + "@" + morningStarDTO.getFundCode() + "@" + morningStarDTO.getFundName(), morningStarDTO.toStr());
//                page.putField(morningStarDTO.getFundCode(), morningStarDTO.getFundCode());
//                page.putField(morningStarDTO.getFundName(), morningStarDTO.getFundName());
//                page.putField(morningStarDTO.getReturn1Day(), morningStarDTO.getReturn1Day());
//                page.putField(morningStarDTO.getReturn1Week(), morningStarDTO.getReturn1Week());
//                page.putField(morningStarDTO.getReturn1Month(), morningStarDTO.getReturn1Month());
//                page.putField(morningStarDTO.getReturn3Month(), morningStarDTO.getReturn3Month());
//                page.putField(morningStarDTO.getReturn6Month(), morningStarDTO.getReturn6Month());
//                page.putField(morningStarDTO.getReturn1Year(), morningStarDTO.getReturn1Year());
//                page.putField(morningStarDTO.getReturn2Year(), morningStarDTO.getReturn2Year());
//                page.putField(morningStarDTO.getReturn3Year(), morningStarDTO.getReturn3Year());
//                page.putField(morningStarDTO.getReturn5Year(), morningStarDTO.getReturn5Year());
//                page.putField(morningStarDTO.getReturn10Year(), morningStarDTO.getReturn10Year());
//                page.putField(morningStarDTO.getReturnInception(), morningStarDTO.getReturnInception());
            }

            //////////////////////////// 当前页面数据采集完毕，构建翻页请求 //////////////////////////////////////
            /* 模拟翻页请求JavaScript */
            // 获取下一页元素(Form Submit Function)
            int last = html.xpath("div[@id='qr_pager']/*/div[@id='ctl00_cphMain_AspNetPager1']/a").all().size();
            last--;
            String navPageJavaScriptRequest = html.xpath("div[@id='qr_pager']/*/div[@id='ctl00_cphMain_AspNetPager1']/a[" + last + "]/@href").get();
            log.debug("nav page js request :: {}", navPageJavaScriptRequest);

            // 为空时，翻页请求是最后一页。
            log.debug("TotalResult counts {} ,NavPage {} ,max page {}", Integer.valueOf(html.xpath("div[@id='qr_pager']/*/span[@id='ctl00_cphMain_TotalResultLabel']/text()").get()), aiNavPageIndex.get(), navPageMaxValue);
            if (StringUtils.isEmpty(navPageJavaScriptRequest)) {
                // mock
                navPageJavaScriptRequest = "javascript:__doPostBack('999999','999999')";
            }

            aiNavPageIndex.incrementAndGet();
            // 使用form表单提交的方式
            request = setFormPostRequest(navPageJavaScriptRequest, html);
            // 将Request加入Scheduler中
            /* 部分三：从页面发现后续的url地址来抓取 */
            page.addTargetRequest(request);
        } else {
            // 设置skip之后，这个页面的结果不会被Pipeline处理
            page.setSkip(Boolean.TRUE);
        }
    }

    /**
     * 采集数据
     *
     * @param html
     * @return
     */
    private List<MorningStarExtendDomain> dataAgent(Html html) {
        // 根据每页数量定义List大小
        List<MorningStarExtendDomain> morningStarDTOList = new ArrayList<>(Integer.valueOf(html.xpath("select[@name='ctl00$cphMain$ddlPageSite']/option[@selected='selected']/@value").get()));

        /*
        <td class="msDataText" width="60"><a href="/quicktake/0P0001696E" target="_blank">001410</a></td>
        <td class="msDataText" width="150"><a href="/quicktake/0P0001696E" target="_blank">信达澳银新能源产业股票</a></td>
        <td class="msDataNumeric" width="45">-4.77</td>
        <td class="msDataNumeric" width="45">5.20</td>
        <td class="msDataNumeric" width="45">19.27</td>
        <td class="msDataNumeric" width="45">60.53</td>
        <td class="msDataNumeric" width="45">77.30</td>
        <td class="msDataNumeric" width="45">104.94</td>
        <td class="msDataNumeric" width="55">49.16</td>
        <td class="msDataNumeric" width="55">44.15</td>
        <td class="msDataNumeric" width="55">-</td>
        <td class="msDataNumeric" width="60">-</td>
        <td class="msDataNumeric" width="60">230.41</td>
         */
        Selectable ctl00CphMainGridResultSelectable = html.xpath("table[@id='ctl00_cphMain_gridResult']");
        List<String> trs = ctl00CphMainGridResultSelectable.xpath("tr").all();
        // i从1开始，0为header，1为body数据
        for (int i = 1; i < trs.size(); i++) {
            log.debug("tr :: {}", trs.get(i));
            // 为了让Html.create构建出html元素，补充table元素
            String htmlTableTemplate = "<table>{0}</table>";
            Html trHtml = Html.create(htmlTableTemplate.replace("{0}", trs.get(i)));
            List<String> tds = trHtml.xpath("td").all();

            // 基金ID
            String fundId = "";
            // 基金代码
            String fundCode = "";
            // 基金名称
            String fundName = "";
            // 1天回报(%)
            String return1Day = "";
            // 1周回报(%)
            String return1Week = "";
            // 1个月回报(%)
            String return1Month = "";
            // 3个月回报(%)
            String return3Month = "";
            // 6个月回报(%)
            String return6Month = "";
            // 1年回报(%)
            String return1Year = "";
            // 2年回报(%)
            String return2Year = "";
            // 3年回报(%)
            String return3Year = "";
            // 5年回报(%)
            String return5Year = "";
            // 10年回报(%)
            String return10Year = "";
            // 设立以来
            String returnInception = "";

            // 排除无用td
            // td1： RowNo
            // td2: checkbox
            // td n-2: 三年标准差(%)
            // td n-1: 三年晨星风险系数
            for (int j = 2; j < tds.size() - 2; j++) {
                log.debug("td :: {}", tds.get(j));
                // 为了让Html.create构建出html元素，补充table元素
                String htmlTableTrTemplate = "<table><tr>{0}</tr></table>";
                Html tdHtml = Html.create(htmlTableTrTemplate.replace("{0}", tds.get(j)));
                // 取得数据
                String tdValue = tdHtml.xpath("td/a/text() | td/text()").get();
                log.debug("td value :: {}", tdValue);

                switch (j) {
                    case 2:
                        fundId = tdHtml.xpath("td/a/@href").get().split("/")[2];
                        fundCode = tdValue;
                        log.debug("fundId >> {}", fundId);
                        break;
                    case 3:
                        fundName = tdValue;
                        break;
                    case 4:
                        return1Day = tdValue;
                        break;
                    case 5:
                        return1Week = tdValue;
                        break;
                    case 6:
                        return1Month = tdValue;
                        break;
                    case 7:
                        return3Month = tdValue;
                        break;
                    case 8:
                        return6Month = tdValue;
                        break;
                    case 9:
                        return1Year = tdValue;
                        break;
                    case 10:
                        return2Year = tdValue;
                        break;
                    case 11:
                        return3Year = tdValue;
                        break;
                    case 12:
                        return5Year = tdValue;
                        break;
                    case 13:
                        return10Year = tdValue;
                        break;
                    case 14:
                        returnInception = tdValue;
                        break;
                    default:
                        break;
                }
            }

            // 保存采集对象结果
            MorningStarExtendDomain morningStarDTO = new MorningStarExtendDomain();
            morningStarDTO.setFundId(fundId);
            morningStarDTO.setPage(String.valueOf(aiNavPageIndex.get()));
            morningStarDTO.setFundCode(fundCode);
            morningStarDTO.setFundName(fundName);
            morningStarDTO.setReturn1Day(return1Day);
            morningStarDTO.setReturn1Week(return1Week);
            morningStarDTO.setReturn1Month(return1Month);
            morningStarDTO.setReturn3Month(return3Month);
            morningStarDTO.setReturn6Month(return6Month);
            morningStarDTO.setReturn1Year(return1Year);
            morningStarDTO.setReturn2Year(return2Year);
            morningStarDTO.setReturn3Year(return3Year);
            morningStarDTO.setReturn5Year(return5Year);
            morningStarDTO.setReturn10Year(return10Year);
            morningStarDTO.setReturnInception(returnInception);

            morningStarDTOList.add(morningStarDTO);
        }

        return morningStarDTOList;
    }

    /**
     * 使用form表单提交的方式
     *
     * @param activeTabHref format: href="javascript:__doPostBack('ctl00$cphMain$lbPerformance','')"
     * @param html
     * @return
     */
    private Request setFormPostRequest(String activeTabHref, Html html) {

        String formParam = StringUtils.substringBetween(activeTabHref, "(", ")");
        String[] formParams = formParam.split(Constants.DELIMITING_COMMA.getCode());
        Map<String, Object> params = new HashedMap();
        params.put("__EVENTTARGET", formParams[0].replaceAll("[']", ""));
        params.put("__EVENTARGUMENT", formParams[1].replaceAll("[']", ""));
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

    public void run() {

        Spider morningStarSpider = Spider.create(new MorningStarExtendDataPageProcessor())
                .addUrl(URL);   // add url to Scheduler

        // Monitor for JMX
//        SpiderMonitor.instance().register(morningStarSpider);

        morningStarSpider
//                .setScheduler()   // Scheduler包括两个作用： 对待抓取的URL队列进行管理, 对已抓取的URL进行去重。
//                .setDownloader()  // Downloader负责从互联网上下载页面。如：SeleniumDownloader
                .addPipeline(new ConsolePipeline()) // 输出结果到控制台
                .addPipeline(new TextFilePipeline(FILE_PATH, FILE_NAME))  // 使用Pipeline保存结果到文件
                .thread(Constants.THREADS.getCount())
                .run();
    }
}
