package com.sun.caishenye.octopus.stock.agent.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;
import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.cache.StockCache;
import com.sun.caishenye.octopus.stock.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RestTemplate 采集 (call api)
 */
@Component
@Slf4j
public class ApiRestTemplate {

    // 雪球 十大股东
    // 最新
    // http://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol=SZ000010&circula=0&count=200
    // 十大股东
    // http://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol=SZ000010&locate=1711814400000&start=1711814400000&circula=0
    private static final String XUEQIU_SDGD_URL = "http://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol={location}{companyCode}&circula=0&count=200";
    private static final String XUEQIU_SDGD_URL2 = "http://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol={location}{companyCode}&locate=1711814400000&start=1711814400000&circula=0";

    // 雪球 实时行情
    // https://stock.xueqiu.com/v5/stock/quote.json?symbol=SZ002233&extend=detail
    private static final String XUEQIU_QUOTE_URL = "http://stock.xueqiu.com/v5/stock/quote.json?symbol={location}{companyCode}&extend=detail";
    // 雪球 历史行情(年)
    // https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SZ001289&begin=1751986948511&period=year&type=before&count=-624&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance

    // 雪球 分红配股
    // https://stock.xueqiu.com/v5/stock/f10/cn/bonus.json?symbol=SZ002032&size=1000&page=1&extend=true
    private static final String XUEQIU_BONUS_URL = "http://stock.xueqiu.com/v5/stock/f10/cn/bonus.json?symbol={location}{companyCode}&size=1000&page=1&extend=true";

    // 沪深A股 东方财富网
    /*
    f1:
    f2:昨收
    f3:涨跌幅
    f4:涨跌额
    f5:成交量
    f6:成交额
    f7:振幅
    f8:换手率
    f9:市盈率(动)
    f10:量比
    f11:
    f12:公司代码
    f13:证券交易所
    f14:公司简称
    f15:最高
    f16:最低
    f17:今开
    f18:昨收
    f20:总市值
    f21:流通市值
    f22:
    f23:市净率
    f24:
    f25:
    f62:今日主力净流入
    f115:市盈率(TTM)
    f128:
    f140:
    f141:
    f136:
    f152:
     */
    private static final String EASTMONEY_BASE_LIST_URL  = "http://10.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112408506576043032625_{now}&pn={index}&pz=100&po=0&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f12&fs=m:0+t:6,m:0+t:13,m:0+t:80,m:1+t:2,m:1+t:23&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152&_={now5}";

//    // 历史行情 金融界
//    // http://flashdata2.jrj.com.cn/history/js/share/601628/other/dayk_ex.js?random=1585145121921
//    protected static final String JRJ_HHQ_URL = "http://flashdata2.jrj.com.cn/history/js/share/{companyCode}/other/dayk_ex.js?random={random}";
    // 历史行情 东方财富网
    // http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg=0&end=20500101&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid=0.300308&klt=101&fqt=1&cb=jsonp1688913443970
    protected static final String EASTMONEY_HHQ_URL = "http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg=0&end=20490101&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid={exchange}.{companyCode}&klt=101&fqt=1&cb=jsonp1688913443970";

    // 财务数据(业绩报表) 东方财富网
//    // http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=YJBB21_YJBB&token=70f12f2f4f091e459a279469fe49eca5&filter=(scode=600000)&st=reportdate&sr=-1&p=1&ps=500&js=var%20ITnKjhqD={pages:(tp),data:%20(x),font:(font)}&rt=52946252
//    protected static final String EASTMONEY_FR_YJBB_URL = "http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=YJBB21_YJBB&token=70f12f2f4f091e459a279469fe49eca5&filter=(scode={companyCode})&st=reportdate&sr=-1&p=1&ps=500&js={js}&rt=52946252";
    // http://datacenter.eastmoney.com/api/data/get?type=RPT_LICO_FN_CPD&sty=ALL&p=1&ps=50&st=REPORTDATE&sr=-1&var=fPIShrPs&filter=(SECURITY_CODE=002714)&rt=53437368
    protected static final String EASTMONEY_FR_YJBB_URL_4 = "http://datacenter.eastmoney.com/api/data/get?type=RPT_LICO_FN_CPD&sty=ALL&p=1&ps=500&st=REPORTDATE&sr=-1&var=ITnKjhqD&filter=(SECURITY_CODE={companyCode})&rt={random}";

    // 东方财富网 个股资金流向
    // https://push2his.eastmoney.com/api/qt/stock/fflow/daykline/get?cb=jQuery112307003461005693303_1720105195359&lmt=0&klt=101&fields1=f1,f2,f3,f7&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65&ut=b2884a393a59ad64002292a3e90d46a5&secid=1.601928&_=1720105195360
    protected static final String EASTMONEY_STOCK_MONEY_FLOW_URL = "http://push2his.eastmoney.com/api/qt/stock/fflow/daykline/get?cb=jQuery112307003461005693303_{now}&lmt=0&klt=101&fields1=f1,f2,f3,f7&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65&ut=b2884a393a59ad64002292a3e90d46a5&secid={exchange}.{companyCode}&_={now5}";

    // 历史行情 搜狐
    // http://q.stock.sohu.com/hisHq?code=cn_603999&start=20091126&end=20200325&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.028961481283250157&0.037908320278956964
    protected static final String SOHU_HHQ_URL = "http://q.stock.sohu.com/hisHq?code=cn_{companyCode}&start={startDay}&end={endDay}&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r={random1}&{random2}";

    @Autowired
    private StockCache cache;

    @Autowired
    private ShRestTemplate shRestTemplate;

    @Autowired
    private SzRestTemplate szRestTemplate;

//    @Qualifier("restTemplateText")
//    @Autowired
//    private RestTemplate restTemplateText;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApiOkHttpClient okHttpClient;

    // 沪深A股 东方财富网
    public int getBaseCount() {

        log.debug("stock base data");

        // call rest service
        String jQueryName = Utils.dateTime2Long(LocalDateTime.now().toString()).toString();
        String url =  replaceUrl(EASTMONEY_BASE_LIST_URL, jQueryName, 1);
        String response = okHttpClient.call(url);
        log.debug("call base data response string :: {}", response);
        // 结构化返回值，对返回值进行fmt
        response = StringUtils.removeStart(response, "jQuery112408506576043032625_"+jQueryName+"(");
        response = StringUtils.removeEnd(response, ");");
        log.debug("call base data response :: {}", response);

        Gson gson = new Gson();
        Map<String, Object> responseMap = gson.fromJson(response, Map.class);
        Map<String, Object> data = (Map)responseMap.get("data");
        log.debug("call base data response :: {}", data);

        return ((Double) data.get("total")).intValue();
    }

    private String replaceUrl(String url, String jQueryName, int index) {

        return url.replace("{index}", String.valueOf(index))
                            .replace("{now}", jQueryName)
                            .replace("{now5}", String.valueOf(Long.parseLong(jQueryName) + 5));
    }

    private String replaceUrl(String url, String jQueryName, Map<String, Object> params) {

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            url = url.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }

        return url
                .replace("{now}", jQueryName)
                .replace("{now5}", String.valueOf(Long.parseLong(jQueryName) + 5));
    }

    @Async
    public CompletableFuture<List<StockDomain>> getBaseForObject(int index) {

        log.debug("stock base data");
        String jQueryName = Utils.dateTime2Long(LocalDateTime.now().toString()).toString();
        String url =  replaceUrl(EASTMONEY_BASE_LIST_URL, jQueryName, index);
        // call rest service
        String response;
//        try {
////            response = restTemplateText.getForObject(url, String.class);
//        } catch (ResourceAccessException e) {
            response = okHttpClient.call(url);
//        }
        log.debug("call base data response string :: {}", response);
        // 结构化返回值，对返回值进行fmt
        response = StringUtils.removeStart(response, "jQuery112408506576043032625_"+jQueryName+"(");
        response = StringUtils.removeEnd(response, ");");
        log.debug("call base data response :: {}", response);

        Gson gson = new Gson();
        Map<String, Object> responseMap = gson.fromJson(response, Map.class);
        Map<String, Object> data = (Map)responseMap.get("data");
        log.debug("call base data response :: {}", data);
        List<Map<String, String>> diffs = (List)data.get("diff");
        log.debug("call base diffs response :: {}", diffs);
        List<StockDomain> bases = new ArrayList<>();
//        StockDomain base = new StockDomain();
        for (Map<String, String> diff : diffs) {
            // 公司代码
            String companyCode = diff.get("f12");
//            StockDomain tempBase = null;
//            // 证券交易所
//            if (StringUtils.startsWith(companyCode, "6")) {
//                // 600001/686868
//                tempBase = shRestTemplate.getBaseData(companyCode);
//            } else {
//                // 000002/300002
//                tempBase = szRestTemplate.getBaseData(companyCode);
//            }
//            if (Objects.isNull(tempBase)) {
//                continue;
//            }
            // 流通市值
            if ("-".equals(diff.get("f21"))) {
                log.debug("退市->{},{}", companyCode, diff.get("f14"));
//                return null;
                continue;
            }

            StockDomain base = new StockDomain();
            // 公司代码
            base.setCompanyCode(companyCode);
            // 公司简称
            base.setCompanyName(diff.get("f14"));
            // 证券交易所
            if (StringUtils.startsWith(companyCode, "6")) {
                // 600001/686868
                base.setExchange(Constants.EXCHANGE_SH.getString());
            } else {
                // 000002/300002
                base.setExchange(Constants.EXCHANGE_SZ.getString());
            }
            bases.add(base);
        }
        return CompletableFuture.completedFuture(bases);
    }

    // 财务数据(业绩报表) 东方财富网
    @Async
    public CompletableFuture<List<FinancialReport2Domain>> getFrYjbbForObject4(StockDomain stockDomain) {

        log.debug("call fr yjbb request params :: {}", stockDomain);
        List<FinancialReport2Domain> result = new ArrayList<>();
        try {

            // call rest service
            String jQueryName = Utils.dateTime2Long(LocalDateTime.now().toString()).toString();
            String url =  replaceUrl(EASTMONEY_FR_YJBB_URL_4, jQueryName, hhqUrlBuilder(stockDomain));
            // call rest service
            String response = okHttpClient.call(url);
//            String response = restTemplateText.getForObject(EASTMONEY_FR_YJBB_URL_4, String.class, hhqUrlBuilder(stockDomain));
            log.debug("call fr yjbb response string :: {}", response);
            // 结构化返回值，对返回值进行fmt
            response = StringUtils.removeStart(response, "var ITnKjhqD=");
            response = StringUtils.removeEnd(response, ";");
            log.debug("call fr yjbb response :: {}", response);

            Gson gson = new GsonBuilder()
                    .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                    .create();
            Map<String, Object> responseMap = gson.fromJson(response, Map.class);

            Map<String, Object> resultMap = (Map)responseMap.get("result");
            if (resultMap != null) {
                // page
                Long page = (Long) resultMap.get("pages");
                // 非退市
                if (page.intValue() != 0) {
                    result = frYjbbResultDataBuilder4((List) resultMap.get("data"));
                }
            }
            log.debug("call fr yjbb response value :: {}", result);
//            log.info("call fr yjbb response ::  {} size {}", stockDomain.getCompanyCode(), result.size());
        } catch (JsonSyntaxException e) {
            log.error("getFrYjbbForObject4 :: " + hhqUrlBuilder(stockDomain) + " " + e);
        } catch (RestClientException e) {
            log.error("fr yjbb retry {} :: {}", stockDomain.getCompanyCode(), e.getMessage());
            // 访问异常 retry
//            getFrYjbbForObject4(stockDomain);
        } catch (Exception e) {
            log.error(hhqUrlBuilder(stockDomain) + " " + e);
            throw e;
        }
        return CompletableFuture.completedFuture(result);
    }

    private List<FinancialReport2Domain> frYjbbResultDataBuilder4(List<Map<String, Object>> data) {

        List<FinancialReport2Domain> result = new ArrayList<>(data.size());
        data.forEach(t -> {

            FinancialReport2Domain domain = new FinancialReport2Domain();
            // 股票代码
            domain.setCompanyCode(parseValue(t.get("SECURITY_CODE")));
            // 股票名称
            domain.setCompanyName(parseValue(t.get("SECURITY_NAME_ABBR")));
            // 交易市场
            domain.setTradeMarket(parseValue(t.get("TRADE_MARKET")));
            // 截止日期
            domain.setDeadline(Utils.formatDate(parseValue(t.get("REPORTDATE"))));
            // 所属行业
            domain.setPublishName(parseValue(t.get("PUBLISHNAME")));
            // 首次公告日期
            domain.setFirstNoticeDate(Utils.formatDate(parseValue(t.get("NOTICE_DATE"))));
            // 最新公告日期
            domain.setLatestNoticeDate(Utils.formatDate(parseValue(t.get("UPDATE_DATE"))));
            // 每股收益(元)
            domain.setBasicEps(parseValue(t.get("BASIC_EPS")));
            // 每股收益(扣除)(元)
            domain.setCutBasicEps(parseValue(t.get("DEDUCT_BASIC_EPS")));
            // 主营业务收入
            domain.setMainBusinessIncome(parseValue(t.get("TOTAL_OPERATE_INCOME")));
            // 主营业务收入增长率(%)(同比)
            domain.setMainBusinessIncomeGrowthRate(parseValue(t.get("YSTZ")));
            // 主营业务收入增长率(%)(环比)
            domain.setMainBusinessIncomeGrowthRateMoM(parseValue(t.get("YSHZ")));
            // 净利润
            domain.setNetProfit(parseValue(t.get("PARENT_NETPROFIT")));
            // 净利润增长率(%)(同比)
            domain.setNetProfitGrowthRate(parseValue(t.get("SJLTZ")));
            // 净利润增长率(%)(环比)
            domain.setNetProfitGrowthRateMoM(parseValue(t.get("SJLHZ")));
            // 净资产收益率
            domain.setRoeWeighted(parseValue(t.get("WEIGHTAVG_ROE")));
            // 每股净资产
            domain.setBps(parseValue(t.get("BPS")));
            // 每股现金流量
            domain.setPerShareCashFlowFromOperations(parseValue(t.get("MGJYXJJE")));
            // 销售毛利率
            domain.setGrossProfitMargin(parseValue(t.get("XSMLL")));
            // 利润分配
            domain.setProfitDistribution(parseValue(t.get("ASSIGNDSCRPT")));
            // 股息率
            domain.setDividendYield(parseValue(t.get("ZXGXL")));
            // 净利润率(净利润/主营业务收入)
            domain.setNetMargin(Utils.rate(domain.getNetProfit(), domain.getMainBusinessIncome()));

            result.add(domain);
        });

        return result;
    }

    private String parseValue(Object value) {
        String str = null;
        if (value instanceof String || value instanceof Number) {
            str = String.valueOf(value);
        }

        if ("-".equals(str) || StringUtils.isEmpty(str)) {
            return "0";
        } else if ("不分配不转增".equals(str)) {
            return "不分配";
        } else {
            return str;
        }
    }

//    // TODO 数据延迟，发布日时，无数据
//    // 财务数据(业绩报表) 东方财富网
//    @Async
//    public CompletableFuture<List<FinancialReport2Domain>> getFrYjbbForObject(StockDomain stockDomain) {
//
//        log.debug("call fr yjbb request params :: {}", stockDomain);
//        List<FinancialReport2Domain> result = new ArrayList<>();
//        try {
//
//            // call rest service
//            String response = restTemplate.getForObject(EASTMONEY_FR_YJBB_URL, String.class, frYjbbUrlBuilder(stockDomain));
//            log.debug("call fr yjbb response string :: {}", response);
//            // 结构化返回值，对返回值进行fmt
//            response = StringUtils.removeStart(response, "var ITnKjhqD=");
//            log.debug("call fr yjbb response :: {}", response);
//
//            Gson gson = new Gson();
//            Map<String, Object> responseMap = gson.fromJson(response, Map.class);
//
//            // page
//            Double page = (Double)responseMap.get("pages");
//            // 非退市
//            if (page.intValue() != 0) {
//                // font
//                Map<String, Object> fontMap = (Map)responseMap.get("font");
//                List<Map<String, String>> fontList = (List)fontMap.get("FontMapping");
//                Map<String, String> fontMapping = fontList.stream().collect(
//                Collectors.toMap(t -> String.valueOf(t.get("code")), t -> String.valueOf(t.get("value")).replace(".0", "")));
//
//                result = frYjbbResultDataBuilder(fontMapping, (List)responseMap.get("data"));
//            }
//            log.debug("call fr yjbb response value :: {}", result);
////            log.info("call fr yjbb response ::  {} size {}", stockDomain.getCompanyCode(), result.size());
//        } catch (HttpClientErrorException e) {
//            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
//        } catch (JsonSyntaxException je) {
//            log.error("getFrYjbbForObject :: "+ frYjbbUrlBuilder(stockDomain) + " " + je);
//        } catch (ResourceAccessException ae) {
//            // 访问异常 retry
//            getFrYjbbForObject(stockDomain);
//        }
//        return CompletableFuture.completedFuture(result);
//    }
//
//    private List<FinancialReport2Domain> frYjbbResultDataBuilder(Map<String, String> fontMapping, List<Map<String, String>> data) {
//
//        List<FinancialReport2Domain> result = new ArrayList<>(data.size());
//        data.stream().forEach(t -> {
//
//            FinancialReport2Domain domain = new FinancialReport2Domain();
//            // 股票代码
//            domain.setCompanyCode(t.get("scode"));
//            // 股票名称
//            domain.setCompanyName(t.get("sname"));
//            // 交易市场
//            domain.setTradeMarket(t.get("trademarket"));
//            // 截止日期
//            domain.setDeadline(Utils.formatDate(t.get("reportdate")));
//            // 所属行业
//            domain.setPublishName(t.get("publishname"));
//            // 首次公告日期
//            domain.setFirstNoticeDate(Utils.formatDate(t.get("firstnoticedate")));
//            // 最新公告日期
//            domain.setLatestNoticeDate(Utils.formatDate(t.get("latestnoticedate")));
//            // 每股收益(元)
//            domain.setBasicEps(parseValue(fontMapping, t.get("basiceps")));
//            // 每股收益(扣除)(元)
//            domain.setCutBasicEps(parseValue(fontMapping, t.get("cutbasiceps")));
//            // 主营业务收入
//            domain.setMainBusinessIncome(parseValue(fontMapping, t.get("totaloperatereve")));
//            // 主营业务收入增长率(%)(同比)
//            domain.setMainBusinessIncomeGrowthRate(parseValue(fontMapping, t.get("ystz")));
//            // 主营业务收入增长率(%)(环比)
//            domain.setMainBusinessIncomeGrowthRateMoM(parseValue(fontMapping, t.get("yshz")));
//            // 净利润
//            domain.setNetProfit(parseValue(fontMapping, t.get("parentnetprofit")));
//            // 净利润增长率(%)(同比)
//            domain.setNetProfitGrowthRate(parseValue(fontMapping, t.get("sjltz")));
//            // 净利润增长率(%)(环比)
//            domain.setNetProfitGrowthRateMoM(parseValue(fontMapping, t.get("sjlhz")));
//            // 净资产收益率
//            domain.setRoeWeighted(parseValue(fontMapping, t.get("roeweighted")));
//            // 每股净资产
//            domain.setBps(parseValue(fontMapping, t.get("bps")));
//            // 每股现金流量
//            domain.setPerShareCashFlowFromOperations(parseValue(fontMapping, t.get("mgjyxjje")));
//            // 销售毛利率
//            domain.setGrossProfitMargin(parseValue(fontMapping, t.get("xsmll")));
//            // 利润分配
//            domain.setProfitDistribution(parseValue(fontMapping, t.get("assigndscrpt")));
//            // 股息率
//            domain.setDividendYield(parseValue(fontMapping, t.get("gxl")));
//            // 净利润率(净利润/主营业务收入)
//            domain.setNetMargin(Utils.rate(domain.getNetProfit(), domain.getMainBusinessIncome()));
//
//            result.add(domain);
//        });
//
//        return result;
//    }
//
//    private String parseValue(Map<String, String> fontMapping, String value) {
//
//        if ("-".equals(value)) {
//            return "0";
//        }
//        if ("不分配不转增".equals(value)) {
//            return "不分配";
//        }
//
//        String[] values = value.split(";");
//        StringBuilder sbVal = new StringBuilder();
//        for (String val: values) {
//            if (val.contains("-")) {
//                if (val.charAt(0) == '-' || val.charAt(val.length() - 1) == '-') {
//                    sbVal.append("-");
//                }
//                sbVal.append(fontMapping.get(val.replace("-","") + ";"));
//            } else if (val.contains(".")) {
//                if (val.charAt(0) == '.' || val.charAt(val.length() - 1) == '.') {
//                    sbVal.append(".");
//                }
//                sbVal.append(fontMapping.get(val.replace(".","") + ";"));
//            } else if (val.contains("&#x")) {
//                sbVal.append(fontMapping.get(val + ";"));
//            } else {
//                sbVal.append(fontMapping.get(val));
//            }
//        }
//
//        return sbVal.toString();
//    }
//
//    private Map<String, Object> frYjbbUrlBuilder(StockDomain stockDomain) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("companyCode", stockDomain.getCompanyCode());
//        params.put("js", "var ITnKjhqD={pages:(tp),data: (x),font:(font)}");
//        return params;
//    }
//
//    // 历史行情
//    @Async
//    public CompletableFuture<DayLineDomain> getHhqForObject(StockDomain stockDomain) {
//        log.debug("call hhq request params :: {}", stockDomain);
//        DayLineDomain hhqDomain = new DayLineDomain();
//        try {
//
//            // call rest service
//            String response = restTemplateText.getForObject(JRJ_HHQ_URL, String.class, hhqUrlBuilder(stockDomain));
//            log.debug("call hhq response string :: {}", response);
//            // 结构化返回值，对返回值进行fmt
//            response = StringUtils.removeStart(response, "var s_d_ex_" + stockDomain.getCompanyCode() + "=");
//            response = StringUtils.substringBefore(response,"\"factor\"");
//            response = response.replace("]],","]]}");
//
//            log.debug("call hhq response :: {}", response);
//            Gson gson = new Gson();
//            hhqDomain = gson.fromJson(response, DayLineDomain.class);
//
////            log.debug("call hhq response value :: {}", hhqDomain);
//        } catch (HttpClientErrorException e) {
//            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
//        } catch (JsonSyntaxException je) {
//            log.error("getHhqForObject :: " + hhqUrlBuilder(stockDomain) + " " + je);
//        }
//        return CompletableFuture.completedFuture(hhqDomain);
//    }
//
    // 历史行情
    @Async
    public CompletableFuture<DayLineDomain> getHhqForObject(StockDomain stockDomain) {
        log.debug("call hhq request params :: {}", stockDomain);
        DayLineDomain hhqDomain = new DayLineDomain();
        try {

            // call rest service
            String jQueryName = Utils.dateTime2Long(LocalDateTime.now().toString()).toString();
            String url =  replaceUrl(EASTMONEY_HHQ_URL, jQueryName, hhqUrlBuilder(stockDomain));
            // call rest service
            String response = okHttpClient.call(url);
//            String response = restTemplateText.getForObject(EASTMONEY_HHQ_URL, String.class, hhqUrlBuilder(stockDomain));
            log.debug("call hhq response string :: {}", response);
            // 结构化返回值，对返回值进行fmt
            response = StringUtils.removeStart(response, "jsonp1688913443970(");
            response = StringUtils.removeEnd(response, ");");
            log.debug("call hhq response :: {}", response);
            Gson gson = new Gson();
            Map<String, Object> responseMap = gson.fromJson(response, Map.class);
            Map<String, Object> dataMap = (Map)responseMap.get("data");
            List<String> klines = (List)dataMap.get("klines");

            hhqDomain.setCompanyCode(dataMap.get("code").toString());
            hhqDomain.setCompanyName(dataMap.get("name").toString());

            // 证券交易所
            if (StringUtils.startsWith(dataMap.get("code").toString(), "6")) {
                // 600001/686868
                hhqDomain.getSummary().setId(Constants.EXCHANGE_SH.getString().concat(dataMap.get("code").toString()));
            } else {
                // 000002/300002
                hhqDomain.getSummary().setId(Constants.EXCHANGE_SZ.getString().concat(dataMap.get("code").toString()));
            }

            klines.forEach(k -> {
                String[] hqs = k.split(",");
                hqs[0] = hqs[0].replaceAll("-", "");
                hhqDomain.getHqs().add(hqs);
            });
        } catch (HttpClientErrorException e) {
            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        } catch (JsonSyntaxException je) {
            log.error("getHhqForObject :: " + hhqUrlBuilder(stockDomain) + " " + je);
        }
        return CompletableFuture.completedFuture(hhqDomain);
    }

    private Map<String, Object> hhqUrlBuilder(StockDomain stockDomain) {
        Map<String, Object> params = new HashMap<>();
        String companyCode = stockDomain.getCompanyCode();
        params.put("companyCode", companyCode);
        // 证券交易所
        if (StringUtils.startsWith(companyCode, "6")) {
            // 600001/686868
            params.put("exchange", 1);
        } else {
            // 000002/300002
            params.put("exchange", 0);
        }
        params.put("random", RandomUtils.nextInt());
        return params;
    }

    // 历史行情(指定日期)
    public DayLineDomain getHhqByDateForObject(StockDomain stockDomain, String sDate) {

        int dd = 0;
        LocalDate date = LocalDate.parse(sDate);
        DayLineDomain hhq;
        while (true) {
            date = date.minusDays(dd--);
            stockDomain.getSbDomain().setRegistrationDate(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            hhq = getHhqByDateForObject(stockDomain);
            if (hhq != null) {
                return hhq;
            }
        }
    }

    // 历史行情(指定日期)
    public AnnualIncreaseDomain getHhqByDateForObject(StockDomain stockDomain, String sDate, String eDate) {
        AnnualIncreaseDomain domain = new AnnualIncreaseDomain();
        // 指定日期
        String jQueryName = Utils.dateTime2Long(LocalDateTime.now().toString()).toString();
        String url =  replaceUrl(SOHU_HHQ_URL, jQueryName, hhqUrlBuilderWithSohu(stockDomain, sDate, eDate));
        // call rest service
        String response = okHttpClient.call(url);
        // 结构化返回值，对返回值进行fmt
        response = StringUtils.substringBetween(response, "(",")");
        // 无交易数据
        if (StringUtils.isEmpty(response) || "{}".equals(response)) {
            // 未上市
            domain.setIncrease("-");
        } else {
            JSONArray jsonArray = JSONArray.parseArray(response);
            if (!jsonArray.isEmpty()) {
                Gson gson = new Gson();
                DayLineDomain hhqDomain = gson.fromJson(jsonArray.get(0).toString(), DayLineDomain.class);
                // 无stat节点数据
                if (hhqDomain.getStat() == null) {
                    // call 雪球 TODO
                    domain.setIncrease("x");
                } else {
                    domain.setIncrease(hhqDomain.getStat().get(3).replace("%",""));
                }
            }
        }

        domain.setYear(Utils.getYear(sDate));
        domain.setCompanyCode(stockDomain.getCompanyCode());
        domain.setCompanyName(stockDomain.getCompanyName());
        return domain;
    }

    // 历史行情(指定日期)
    public DayLineDomain getHhqByDateForObject(StockDomain stockDomain) {
        log.debug("call hhq request params :: {}", stockDomain);
        DayLineDomain hhqDomain = null;
        String response = null;
        try {
            Thread.sleep(150);
            // 指定日期
            String jQueryName = Utils.dateTime2Long(LocalDateTime.now().toString()).toString();
            String url =  replaceUrl(SOHU_HHQ_URL, jQueryName, hhqUrlBuilderWithSohu(stockDomain));
            // call rest service
            response = okHttpClient.call(url);
//            response = restTemplateText.getForObject(SOHU_HHQ_URL, String.class, hhqUrlBuilderWithSohu(stockDomain));
            log.debug("call hhq response string :: {}", response);

            // 结构化返回值，对返回值进行fmt
            response = StringUtils.substringBetween(response, "(",")");
            log.debug("call hhq response :: {}", response);

            try {
                JSONArray jsonArray = JSONArray.parseArray(response);
                if (!jsonArray.isEmpty()) {
                    Gson gson = new Gson();
                    log.debug("call hhq response jsonarray value :: {}", jsonArray.get(0).toString());
                    hhqDomain = gson.fromJson(jsonArray.get(0).toString(), DayLineDomain.class);
                    // 有历史数据
                    if (hhqDomain.getHq() != null) {
                        // 收盘日
                        hhqDomain.setDay(Utils.formatDate2String(hhqDomain.getHq().get(0)[0]));
                        // 收盘价
                        hhqDomain.setPrice(hhqDomain.getHq().get(0)[2]);

                        // 数据问题 call jrj
                        if (Double.parseDouble(hhqDomain.getPrice()) >= 2000) {
                            String day = getDay(stockDomain);
                            DayLineDomain tDayLineDomain = getHhqForObject(stockDomain).toCompletableFuture().get();
                            String[] hqs = tDayLineDomain.getHqs().stream().filter(t -> t[0].equals(day)).findFirst().orElse(new String[3]);
                            // 收盘价
                            hhqDomain.setPrice(hqs[2]);
                        }
                    } else {
                        return null;
                    }
                }
            } catch (JSONException je) {
                log.error(hhqUrlBuilderWithSohu(stockDomain) + " " + je);
            } catch (InterruptedException | ExecutionException e) {
                log.error("call getHhqForObject error :: {} ", e.toString());
            }
        } catch (Exception e) {
            // not found, call next api
            if ("{}".equals(response)) {
                AtomicBoolean isOK = new AtomicBoolean(false);
                try {
                    String day = getDay(stockDomain);
                    // 全量
                    DayLineDomain tDayLineDomain = getHhqForObject(stockDomain).get();
                    while (!isOK.get()) {
                        for (String[] t : tDayLineDomain.getHqs()) {
                            if (t[0].equals(day)) {
                                isOK.set(true);
                                // 收盘日
                                hhqDomain.setDay(getDay(stockDomain));
                                // 收盘价
                                hhqDomain.setPrice(t[2]);
                                break;
                            }
                        }
                        if (isOK.get()) {
                            break;
                        } else {
                            day = String.valueOf(Integer.parseInt(day) - 1);
                            log.debug("call getHhqForObject :: {} date :: {} ", stockDomain.getCompanyCode(), day);
                            // 数据质量差，交易所取数据（不保证数据正确）
                            if ("19900101".equals(day)) {
                                // 从 证券交易所 取数据
                                if (tDayLineDomain.getSummary().getId().contains(Constants.EXCHANGE_SZ.getString())) {
                                    // call SzRestTemplate
                                    SzHqDomain hqDomain = szRestTemplate.getHhqData(stockDomain, null);
                                    if (hqDomain != null) {
                                        // 收盘价
                                        hhqDomain.setPrice(hqDomain.getPrice());
                                        isOK.set(true);
                                    }
                                } else {
                                    String day2 = getDay(stockDomain);
                                    // call ShRestTemplate
                                    long days = ChronoUnit.DAYS.between(LocalDate.of(Integer.parseInt(day2.substring(0, 4)),
                                                    Integer.parseInt(day2.substring(4, 6)), Integer.parseInt(day2.substring(6, 8))),
                                            LocalDate.now());
                                    ShHqDomain shHqDomain = shRestTemplate.getHhqData(stockDomain, days);
                                    if (shHqDomain != null) {
                                        // 收盘价
                                        DayLineDomain finalHhqDomain = hhqDomain;
                                        shHqDomain.getKline().forEach(t -> {
                                            if (day2.equals(t[0])) {
                                                finalHhqDomain.setPrice(t[3]);
                                                isOK.set(true);
                                            }
                                        });
                                    }
                                }
                                // 收盘日
                                hhqDomain.setDay(getDay(stockDomain));
                                break;
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e2) {
                    log.error("call getHhqForObject error :: {}", e2.toString());
                    return null;
                }
                return isOK.get() ? hhqDomain : null;
            } else if ("{\"status\":3,\"msg\":\"begin time invalid\"}".equals(response)) {
                return null;
            }
        }
//        log.debug("call hhq response value :: {}", hhqDomain);
        return hhqDomain;
    }

    private Map<String, Object> hhqUrlBuilderWithSohu(StockDomain stockDomain) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("startDay", getDay(stockDomain));
        params.put("endDay", getDay(stockDomain));
        params.put("random1", RandomUtils.nextInt());
        params.put("random2", RandomUtils.nextInt());
        return params;
    }

    private Map<String, Object> hhqUrlBuilderWithSohu(StockDomain stockDomain, String sDate, String eDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("startDay", sDate);
        params.put("endDay", eDate);
        params.put("random1", RandomUtils.nextInt());
        params.put("random2", RandomUtils.nextInt());
        return params;
    }

    private String getDay(StockDomain stockDomain) {
        return Utils.formatDate2String(stockDomain.getSbDomain().getRegistrationDate());
    }

    // 分红配股
    public List<ShareBonusDomain> getShareBonus(String companyCode, String exchange) {

        List<ShareBonusDomain> sbList1 = getSB(companyCode, exchange);
        List<ShareBonusDomain> sbList2 = getSB(companyCode, exchange);
        List<ShareBonusDomain> sbList3 = getSB(companyCode, exchange);

        Map<Integer, List<ShareBonusDomain>> max = new HashMap<>();
        max.putIfAbsent(sbList1.size(), sbList1);
        max.putIfAbsent(sbList2.size(), sbList2);
        max.putIfAbsent(sbList3.size(), sbList3);

        return max.get(max.keySet().stream().mapToInt(v -> v).max().orElse(sbList1.size()));
    }

    // 分红配股
    private List<ShareBonusDomain> getSB(String companyCode, String exchange) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cache.getXQCookies());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(XUEQIU_BONUS_URL, HttpMethod.GET, entity, Map.class,
                builderShareBonusUrl(companyCode, exchange));
        Map<String, Object> responseMap = responseEntity.getBody();
        Map<String, Object> dataMap = (Map)responseMap.get("data");
        List<Map<String, Object>> items = (List)dataMap.get("items");

        List<ShareBonusDomain> sbList = new ArrayList<>();
        for (Map item: items) {
            ShareBonusDomain sb = new ShareBonusDomain();
            sb.setDividendYear(item.get("dividend_year").toString());
            sb.setDividendDate(Utils.long2Date((Long)item.get("ex_dividend_date")));
            sb.setRegistrationDate(Utils.long2Date((Long)item.get("equity_date")));

            sbList.add(sb);
        }
        return sbList;
    }

    private Map<String, Object> builderShareBonusUrl(String companyCode, String exchange) {
        Map<String, Object> params = new HashMap<>();
        params.put("location", exchange.toUpperCase());
        params.put("companyCode", companyCode);
        return params;
    }

    // 实时行情
    @Async
    public void getHqData(StockDomain stockDomain, String location) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cache.getXQCookies());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(XUEQIU_QUOTE_URL, HttpMethod.GET, entity, Map.class,
                builderShareBonusUrl(stockDomain.getCompanyCode(), location));
        Map<String, Object> responseMap = responseEntity.getBody();
        Map<String, Object> dataMap = (Map)responseMap.get("data");
        Map<String, Object> quoteMap = (Map)dataMap.get("quote");

        stockDomain.setPrice(String.valueOf(quoteMap.get("current")));
    }

    // 资金流 个股
    @Async
    public CompletableFuture<MoneyFlowDomain> getStockMoneyFlow(StockDomain stockDomain) {

        MoneyFlowDomain domain = new MoneyFlowDomain();
        String jQueryName = Utils.dateTime2Long(LocalDateTime.now().toString()).toString();
        String url =  replaceUrl(EASTMONEY_STOCK_MONEY_FLOW_URL, jQueryName, hhqUrlBuilder(stockDomain));
        // call rest service
        String response = okHttpClient.call(url);
//        String response = restTemplateText.getForObject(EASTMONEY_STOCK_MONEY_FLOW_URL, String.class, hhqUrlBuilder(stockDomain));
        // 结构化返回值，对返回值进行fmt
        response = StringUtils.removeStart(response, "jQuery112307003461005693303_"+jQueryName+"(");
        response = StringUtils.removeEnd(response, ");");
        Gson gson = new Gson();
        Map<String, Object> responseMap = gson.fromJson(response, Map.class);
        Map<String, Object> dataMap = (Map)responseMap.get("data");

        if (dataMap == null) {
            return CompletableFuture.completedFuture(domain);
        }

        stockDomain.setCompanyName(dataMap.get("name").toString());
        domain.setCompanyCode(dataMap.get("code").toString());
        domain.setCompanyName(dataMap.get("name").toString());
        List<String> klines = (List)dataMap.get("klines");
        domain.setKlines(klines);

        return CompletableFuture.completedFuture(domain);
    }

    // 十大股东
    @Async
    public CompletableFuture<TenHolderDomain> getTenHolder(StockDomain stockDomain) {

        // call rest service
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cache.getXQCookies());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(XUEQIU_SDGD_URL, HttpMethod.GET, entity, Map.class,
                builderShareBonusUrl(stockDomain.getCompanyCode(), stockDomain.getExchange()));
        Map<String, Object> responseMap = responseEntity.getBody();
        Map<String, Object> dataMap = (Map)responseMap.get("data");
        List<Map<String, Object>> items = (List)dataMap.get("items");
        List<Map<String, Object>> times = (List)dataMap.get("times");

        if (items.size() < 5) {
            responseEntity = restTemplate.exchange(XUEQIU_SDGD_URL2, HttpMethod.GET, entity, Map.class,
                    builderShareBonusUrl(stockDomain.getCompanyCode(), stockDomain.getExchange()));
            responseMap = responseEntity.getBody();
            dataMap = (Map)responseMap.get("data");
            items = (List)dataMap.get("items");
            times = (List)dataMap.get("times");
        }

        if (items.isEmpty()) {
            return null;
        }

        TenHolderDomain domain = new TenHolderDomain();
        domain.setCompanyCode(stockDomain.getCompanyCode());
        domain.setCompanyName(stockDomain.getCompanyName());
        domain.setTime(times.get(0).get("name").toString());

        Map<String, Object> mItem;
        TenHolderDomain.Item item;

        if (!items.isEmpty()) {
            mItem = items.get(0);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem1(item);
        }

        if (items.size() > 1) {
            mItem = items.get(1);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem2(item);
        }

        if (items.size() > 2) {
            mItem = items.get(2);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem3(item);
        }

        if (items.size() > 3) {
            mItem = items.get(3);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem4(item);
        }

        if (items.size() > 4) {
            mItem = items.get(4);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem5(item);
        }

        if (items.size() > 5) {
            mItem = items.get(5);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem6(item);
        }

        if (items.size() > 6) {
            mItem = items.get(6);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem7(item);
        }

        if (items.size() > 7) {
            mItem = items.get(7);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem8(item);
        }

        if (items.size() > 8) {
            mItem = items.get(8);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem9(item);
        }

        if (items.size() > 9) {
            mItem = items.get(9);
            item = new TenHolderDomain.Item();
            item.setHolderName(mItem.get("holder_name").toString());
            item.setHeldNum(mItem.get("held_num").toString());
            item.setHeldRatio(mItem.get("held_ratio").toString());
            item.setChg(String.valueOf(mItem.get("chg")));
            domain.setItem10(item);
        }

        return CompletableFuture.completedFuture(domain);
    }
}