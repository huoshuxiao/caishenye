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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * RestTemplate 采集 (call api)
 */
@Component
@Slf4j
public class ApiRestTemplate {

    // 历史行情 金融界
    // http://flashdata2.jrj.com.cn/history/js/share/601628/other/dayk_ex.js?random=1585145121921
    protected static final String JRJ_HHQ_URL = "http://flashdata2.jrj.com.cn/history/js/share/{companyCode}/other/dayk_ex.js?random={random}";

    // 历史行情 搜狐
    // http://q.stock.sohu.com/hisHq?code=cn_603999&start=20091126&end=20200325&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.028961481283250157&0.037908320278956964
    protected static final String SOHU_HHQ_URL = "http://q.stock.sohu.com/hisHq?code=cn_{companyCode}&start={startDay}&end={endDay}&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r={random1}&{random2}";

    // 财务数据(业绩报表) 东方财富网
    // http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=YJBB21_YJBB&token=70f12f2f4f091e459a279469fe49eca5&filter=(scode=600000)&st=reportdate&sr=-1&p=1&ps=500&js=var%20ITnKjhqD={pages:(tp),data:%20(x),font:(font)}&rt=52946252
    protected static final String EASTMONEY_FR_YJBB_URL = "http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=YJBB21_YJBB&token=70f12f2f4f091e459a279469fe49eca5&filter=(scode={companyCode})&st=reportdate&sr=-1&p=1&ps=500&js={js}&rt=52946252";

    @Autowired
    private ShRestTemplate shRestTemplate;

    @Autowired
    private SzRestTemplate szRestTemplate;

    @Qualifier("restTemplateText")
    @Autowired
    private RestTemplate restTemplate;

    // 财务数据(业绩报表) 东方财富网
    @Async
    public CompletableFuture<List<FinancialReport2Domain>> getFrYjbbForObject(StockDomain stockDomain) {

        log.debug("call fr yjbb request params :: {}", stockDomain);
        List<FinancialReport2Domain> result = new ArrayList<>();
        try {

            // call rest service
            String response = restTemplate.getForObject(EASTMONEY_FR_YJBB_URL, String.class, frYjbbUrlBuilder(stockDomain));
            log.debug("call fr yjbb response string :: {}", response);
            // 结构化返回值，对返回值进行fmt
            response = StringUtils.removeStart(response, "var ITnKjhqD=");
            log.debug("call fr yjbb response :: {}", response);

            Gson gson = new Gson();
            Map<String, Object> responseMap = gson.fromJson(response, Map.class);

            // page
            Double page = (Double)responseMap.get("pages");
            // 非退市
            if (page.intValue() != 0) {
                // font
                Map<String, Object> fontMap = (Map)responseMap.get("font");
                List<Map<String, String>> fontList = (List)fontMap.get("FontMapping");
                Map<String, String> fontMapping = fontList.stream().collect(Collectors.toMap(t -> String.valueOf(t.get("code")), t -> String.valueOf(t.get("value")).replace(".0", "")));

                result = frYjbbResultDataBuilder(fontMapping, (List)responseMap.get("data"));
            }
            log.debug("call fr yjbb response value :: {}", result);
//            log.info("call fr yjbb response ::  {} size {}", stockDomain.getCompanyCode(), result.size());
        } catch (HttpClientErrorException e) {
            log.error(stockDomain.getCompanyCode() + " " + e.getRawStatusCode());
        } catch (JsonSyntaxException je) {
            log.error(frYjbbUrlBuilder(stockDomain) + " " + je);
        } catch (ResourceAccessException ae) {
            // 访问异常 retry
            getFrYjbbForObject(stockDomain);
        }
        return CompletableFuture.completedFuture(result);
    }

    private List<FinancialReport2Domain> frYjbbResultDataBuilder(Map<String, String> fontMapping, List<Map<String, String>> data) {

        List<FinancialReport2Domain> result = new ArrayList<>(data.size());
        data.stream().forEach(t -> {

            FinancialReport2Domain domain = new FinancialReport2Domain();
            // 股票代码
            domain.setCompanyCode(t.get("scode"));
            // 股票名称
            domain.setCompanyName(t.get("sname"));
            // 交易市场
            domain.setTradeMarket(t.get("trademarket"));
            // 截止日期
            domain.setDeadline(Utils.formatDate(t.get("reportdate")));
            // 所属行业
            domain.setPublishName(t.get("publishname"));
            // 首次公告日期
            domain.setFirstNoticeDate(Utils.formatDate(t.get("firstnoticedate")));
            // 最新公告日期
            domain.setLatestNoticeDate(Utils.formatDate(t.get("latestnoticedate")));
            // 每股收益(元)
            domain.setBasicEps(parseFloat(fontMapping, t.get("basiceps")));
            // 每股收益(扣除)(元)
            domain.setCutBasicEps(parseFloat(fontMapping, t.get("cutbasiceps")));
            // 主营业务收入
            domain.setMainBusinessIncome(parseFloat(fontMapping, t.get("totaloperatereve")));
            // 主营业务收入增长率(%)(同比)
            domain.setMainBusinessIncomeGrowthRate(parseFloat(fontMapping, t.get("ystz")));
            // 主营业务收入增长率(%)(环比)
            domain.setMainBusinessIncomeGrowthRateMoM(parseFloat(fontMapping, t.get("yshz")));
            // 净利润
            domain.setNetProfit(parseFloat(fontMapping, t.get("parentnetprofit")));
            // 净利润增长率(%)(同比)
            domain.setNetProfitGrowthRate(parseFloat(fontMapping, t.get("sjltz")));
            // 净利润增长率(%)(环比)
            domain.setNetProfitGrowthRateMoM(parseFloat(fontMapping, t.get("sjlhz")));
            // 净资产收益率
            domain.setRoeWeighted(parseFloat(fontMapping, t.get("roeweighted")));
            // 每股净资产
            domain.setBps(parseFloat(fontMapping, t.get("bps")));
            // 每股现金流量
            domain.setPerShareCashFlowFromOperations(parseFloat(fontMapping, t.get("mgjyxjje")));
            // 销售毛利率
            domain.setGrossProfitMargin(parseFloat(fontMapping, t.get("xsmll")));
            // 利润分配
            domain.setProfitDistribution(parseFloat(fontMapping, t.get("assigndscrpt")));
            // 股息率
            domain.setDividendYield(parseFloat(fontMapping, t.get("gxl")));
            // 净利润率(净利润/主营业务收入)
            domain.setNetMargin(Utils.rate(domain.getNetProfit(), domain.getMainBusinessIncome()));

            result.add(domain);
        });

        return result;
    }

    private String parseFloat(Map<String, String> fontMapping, String value) {

        if ("-".equals(value)) {
            return "0";
        }
        if ("不分配不转增".equals(value)) {
            return "不分配";
        }

        String[] values = value.split(";");
        StringBuilder sbVal = new StringBuilder();
        for (String val: values) {
            if (val.contains("-")) {
                if (val.charAt(0) == '-' || val.charAt(val.length() - 1) == '-') {
                    sbVal.append("-");
                }
                sbVal.append(fontMapping.get(val.replace("-","") + ";"));
            } else if (val.contains(".")) {
                if (val.charAt(0) == '.' || val.charAt(val.length() - 1) == '.') {
                    sbVal.append(".");
                }
                sbVal.append(fontMapping.get(val.replace(".","") + ";"));
            } else if (val.contains("&#x")) {
                sbVal.append(fontMapping.get(val + ";"));
            } else {
                sbVal.append(fontMapping.get(val));
            }
        }

        return sbVal.toString();
    }

    private Map<String, Object> frYjbbUrlBuilder(StockDomain stockDomain) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", stockDomain.getCompanyCode());
        params.put("js", "var ITnKjhqD={pages:(tp),data: (x),font:(font)}");
        return params;
    }

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
            hhqUrlBuilder(stockDomain);
            log.error(hhqUrlBuilder(stockDomain) + " " + je);
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
                        SzHqDomain hqDomain = szRestTemplate.getHhqForObject(stockDomain);
                        if (hqDomain != null) {
                            // 收盘价
                            hhqDomain.setPrice(hqDomain.getPrice());
                            isOK.set(true);
                        }

                    } else {

                        // call ShRestTemplate
                        long days = ChronoUnit.DAYS.between(LocalDate.of(Integer.valueOf(day.substring(0, 4)), Integer.valueOf(day.substring(4, 6)), Integer.valueOf(day.substring(6, 8))),
                                LocalDate.now());
                        ShHqDomain shHqDomain = shRestTemplate.getHhqForObject(stockDomain, days);
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
        }

        DayLineDomain hhqDomain = null;
        try {
            JSONArray jsonArray = JSONArray.parseArray(response);
            if (jsonArray.size() > 0) {
                Gson gson = new Gson();
                log.debug("call hhq response jsonarray value :: {}", jsonArray.get(0).toString());
                hhqDomain = gson.fromJson(jsonArray.get(0).toString(), DayLineDomain.class);
                // 收盘日
                hhqDomain.setDay(Utils.formatDate2String(hhqDomain.getHq().get(0)[0]));
                // 收盘价
                hhqDomain.setPrice(hhqDomain.getHq().get(0)[2]);
            }
        } catch (JSONException je) {
            hhqUrlBuilder(stockDomain);
            log.error(hhqUrlBuilder(stockDomain) + " " + je);
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
        return Utils.formatDate2String("--".equals(stockDomain.getSbDomain().getDividendDate()) ? stockDomain.getSbDomain().getRegistrationDate() : stockDomain.getSbDomain().getDividendDate());
    }
}