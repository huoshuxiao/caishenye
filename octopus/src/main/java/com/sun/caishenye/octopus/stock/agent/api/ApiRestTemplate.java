package com.sun.caishenye.octopus.stock.agent.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.stock.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RestTemplate 采集 (call api)
 */
@Component
@Slf4j
public class ApiRestTemplate {

    // 沪深A股 东方财富网
    private static final String EASTMONEY_BASE_LIST_URL = "http://10.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112408506576043032625_1612278160710&pn=1&pz=10000&po=0&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f12&fs=m:0+t:6,m:0+t:13,m:0+t:80,m:1+t:2,m:1+t:23&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152&_=1612278160715";

    // 历史行情 金融界
    // http://flashdata2.jrj.com.cn/history/js/share/601628/other/dayk_ex.js?random=1585145121921
    protected static final String JRJ_HHQ_URL = "http://flashdata2.jrj.com.cn/history/js/share/{companyCode}/other/dayk_ex.js?random={random}";

    // 历史行情 搜狐
    // http://q.stock.sohu.com/hisHq?code=cn_603999&start=20091126&end=20200325&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.028961481283250157&0.037908320278956964
    protected static final String SOHU_HHQ_URL = "http://q.stock.sohu.com/hisHq?code=cn_{companyCode}&start={startDay}&end={endDay}&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r={random1}&{random2}";

    // 财务数据(业绩报表) 东方财富网
    // http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=YJBB21_YJBB&token=70f12f2f4f091e459a279469fe49eca5&filter=(scode=600000)&st=reportdate&sr=-1&p=1&ps=500&js=var%20ITnKjhqD={pages:(tp),data:%20(x),font:(font)}&rt=52946252
    protected static final String EASTMONEY_FR_YJBB_URL = "http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=YJBB21_YJBB&token=70f12f2f4f091e459a279469fe49eca5&filter=(scode={companyCode})&st=reportdate&sr=-1&p=1&ps=500&js={js}&rt=52946252";

    // http://datacenter.eastmoney.com/api/data/get?type=RPT_LICO_FN_CPD&sty=ALL&p=1&ps=50&st=REPORTDATE&sr=-1&var=fPIShrPs&filter=(SECURITY_CODE=002714)&rt=53437368
    protected static final String EASTMONEY_FR_YJBB_URL_4 = "http://datacenter.eastmoney.com/api/data/get?type=RPT_LICO_FN_CPD&sty=ALL&p=1&ps=500&st=REPORTDATE&sr=-1&var=ITnKjhqD&filter=(SECURITY_CODE={companyCode})&rt={random}";

    @Autowired
    private ShRestTemplate shRestTemplate;

    @Autowired
    private SzRestTemplate szRestTemplate;

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplate;

    // 沪深A股 东方财富网
    @Async
    public CompletableFuture<List<StockDomain>> getBaseForObject() {

        log.debug("stock base data");
        // call rest service
        String response = restTemplate.getForObject(EASTMONEY_BASE_LIST_URL, String.class);
        log.debug("call base data response string :: {}", response);
        // 结构化返回值，对返回值进行fmt
        response = StringUtils.removeStart(response, "jQuery112408506576043032625_1612278160710(");
        response = StringUtils.removeEnd(response, ");");
        log.debug("call base data response :: {}", response);

        Gson gson = new Gson();
        Map<String, Object> responseMap = gson.fromJson(response, Map.class);
        Map<String, Object> data = (Map)responseMap.get("data");
        log.debug("call base data response :: {}", data);
        List<Map<String, String>> diffs = (List)data.get("diff");
        log.debug("call base diffs response :: {}", diffs);
        List<StockDomain> bases = new ArrayList<>();
        for (Map<String, String> diff : diffs) {
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
            if ("-".equals(diff.get("f2"))) {
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
            String response = restTemplate.getForObject(EASTMONEY_FR_YJBB_URL_4, String.class, hhqUrlBuilder(stockDomain));
            log.debug("call fr yjbb response string :: {}", response);
            // 结构化返回值，对返回值进行fmt
            response = StringUtils.removeStart(response, "var ITnKjhqD=");
            response = StringUtils.removeEnd(response, ";");
            log.debug("call fr yjbb response :: {}", response);

            Gson gson = new Gson();
            Map<String, Object> responseMap = gson.fromJson(response, Map.class);

            Map<String, Object> resultMap = (Map)responseMap.get("result");
            if (resultMap != null) {
                // page
                Double page = (Double) resultMap.get("pages");
                // 非退市
                if (page.intValue() != 0) {
                    result = frYjbbResultDataBuilder4((List) resultMap.get("data"));
                }
            }
            log.debug("call fr yjbb response value :: {}", result);
//            log.info("call fr yjbb response ::  {} size {}", stockDomain.getCompanyCode(), result.size());
        } catch (HttpClientErrorException e) {
            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        } catch (JsonSyntaxException e) {
            log.error("getFrYjbbForObject4 :: " + hhqUrlBuilder(stockDomain) + " " + e);
        } catch (ResourceAccessException e) {
            // 访问异常 retry
            getFrYjbbForObject4(stockDomain);
        } catch (Exception e) {
            log.error(hhqUrlBuilder(stockDomain) + " " + e);
            throw e;
        }
        return CompletableFuture.completedFuture(result);
    }

    private List<FinancialReport2Domain> frYjbbResultDataBuilder4(List<Map<String, Object>> data) {

        List<FinancialReport2Domain> result = new ArrayList<>(data.size());
        data.stream().forEach(t -> {

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
        if (value instanceof String || value instanceof Double) {
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

    // 历史行情
    @Async
    public CompletableFuture<DayLineDomain> getHhqForObject(StockDomain stockDomain) {
        log.debug("call hhq request params :: {}", stockDomain);
        DayLineDomain hhqDomain = new DayLineDomain();
        try {

            // call rest service
            String response = restTemplate.getForObject(JRJ_HHQ_URL, String.class, hhqUrlBuilder(stockDomain));
            log.debug("call hhq response string :: {}", response);
            // 结构化返回值，对返回值进行fmt
            response = StringUtils.removeStart(response, "var s_d_ex_" + stockDomain.getCompanyCode() + "=");
            response = StringUtils.substringBefore(response,"\"factor\"");
            response = response.replace("]],","]]}");

            log.debug("call hhq response :: {}", response);
            Gson gson = new Gson();
            hhqDomain = gson.fromJson(response, DayLineDomain.class);

            log.debug("call hhq response value :: {}", hhqDomain);
        } catch (HttpClientErrorException e) {
            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        } catch (JsonSyntaxException je) {
            log.error("getHhqForObject :: " + hhqUrlBuilder(stockDomain) + " " + je);
        }
        return CompletableFuture.completedFuture(hhqDomain);
    }

    private Map<String, Object> hhqUrlBuilder(StockDomain stockDomain) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("random", RandomUtils.nextInt());
        return params;
    }

    // 历史行情(指定日期)
    public DayLineDomain getHhqByDateForObject(StockDomain stockDomain, String dividendDateStr) {

        int dd = 0;
        LocalDate dividendDate = LocalDate.parse(dividendDateStr);
        DayLineDomain hhq = null;
        while (hhq == null) {
            dividendDate = dividendDate.minusDays(dd--);
            stockDomain.getSbDomain().setDividendDate(dividendDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            hhq = getHhqByDateForObject(stockDomain);
            if (hhq != null) {
                return hhq;
            }
        }
        return hhq;
    }

    // 历史行情(指定日期)
    public DayLineDomain getHhqByDateForObject(StockDomain stockDomain) {
        log.debug("call hhq request params :: {}", stockDomain);
        // call rest service
        String response = restTemplate.getForObject(SOHU_HHQ_URL, String.class, hhqUrlBuilderWithSohu(stockDomain));
        log.debug("call hhq response string :: {}", response);

        // 结构化返回值，对返回值进行fmt
        response = StringUtils.substringBetween(response, "(",")");
        log.debug("call hhq response :: {}", response);

        // not found, call jrj api
        if ("{}".equals(response)) {
            DayLineDomain hhqDomain = new DayLineDomain();
            AtomicBoolean isOK = new AtomicBoolean(false);
            try {
                String day = getDay(stockDomain);
                DayLineDomain tDayLineDomain = getHhqForObject(stockDomain).get();
                if (tDayLineDomain.getSummary() == null) {return null;}
                tDayLineDomain.getHqs().stream().forEach(t -> {
                    if (t[0].equals(day)) {
                        isOK.set(true);
                        // 收盘日
                        hhqDomain.setDay(day);
                        // 收盘价
                        hhqDomain.setPrice(t[2]);
                        return;
                    }
                });

                // 从 证券交易所 取数据
                if (!isOK.get()) {

                    if (tDayLineDomain.getSummary().getId().contains(Constants.EXCHANGE_SZ.getString())) {

                        // call SzRestTemplate
                        SzHqDomain hqDomain = szRestTemplate.getHhqData(stockDomain);
                        if (hqDomain != null) {
                            // 收盘价
                            hhqDomain.setPrice(hqDomain.getPrice());
                            isOK.set(true);
                        }

                    } else {

                        // call ShRestTemplate
                        long days = ChronoUnit.DAYS.between(LocalDate.of(Integer.valueOf(day.substring(0, 4)), Integer.valueOf(day.substring(4, 6)), Integer.valueOf(day.substring(6, 8))),
                                LocalDate.now());
                        ShHqDomain shHqDomain = shRestTemplate.getHhqData(stockDomain, days);
                        if (shHqDomain != null) {
                            // 收盘价
                            shHqDomain.getKline().stream().forEach(t -> {
                                if (day.equals(t[0])) {
                                    hhqDomain.setPrice(t[3]);
                                    isOK.set(true);
                                }
                            });
                        }
                    }
                    // 收盘日
                    hhqDomain.setDay(day);
                }

            } catch (InterruptedException | ExecutionException e) {
                log.error("call getHhqForObject error " + e);
                return null;
            }
            return isOK.get() == true ? hhqDomain : null;
        } else if ("{\"status\":3,\"msg\":\"begin time invalid\"}".equals(response)) {
            return null;
        }

        DayLineDomain hhqDomain = null;
        try {
            JSONArray jsonArray = JSONArray.parseArray(response);
            if (jsonArray.size() > 0) {
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
            log.error("call getHhqForObject error " + e);
        }
        log.debug("call hhq response value :: {}", hhqDomain);
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

    private String getDay(StockDomain stockDomain) {
        return Utils.formatDate2String("--".equals(stockDomain.getSbDomain().getDividendDate())
                ? stockDomain.getSbDomain().getRegistrationDate()
                : stockDomain.getSbDomain().getDividendDate());
    }
}