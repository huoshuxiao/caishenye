package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 历史行情 - 日K
public class DayLineDomain implements Serializable {

    private static final long serialVersionUID = 4371636697825834191L;

    @Setter
    @Getter
    private BaseDomain summary;

    // 金融界
    // 600000
    //0	:	20200325    收盘日
    //1	:	10.06       昨收
    //2	:	10.15       收盘
    //3	:	10.2        开盘
    //4	:	10.27       最高
    //5	:	10.12       最低
    //6	:	321723      成交量(手)
    //7	:	327768574   成交金额(万)
    //8	:		false
    @Setter
    @Getter
    private List<String[]> hqs;

    // 搜狐
    // 603999
//        0	:	2020-03-25  收盘日
//        1	:	6.02        开盘
//        2	:	6.00        收盘
//        3	:	0.08        涨跌额(元)
//        4	:	1.35%       涨跌额(%)
//        5	:	5.94        最低
//        6	:	6.06        最高
//        7	:	55914       成交量(手)
//        8	:	3349.09     成交金额(万)
//        9	:	0.97%       换手率
    @Setter
    @Getter
    private List<String[]> hq;

    // 公司代码
    @Setter
    @Getter
    private String companyCode;

    // 公司简称
    @Setter
    @Getter
    private String companyName;

    // 收盘价
    @Setter
    @Getter
    private String price;

    // 收盘日  fmt: 20160603
    @Setter
    @Getter
    private String day;

    @Data
    public class BaseDomain implements Serializable {
        private static final long serialVersionUID = -6793387184944636365L;
        // sh601628
        private String id;
        // 601628
        private String code;
        // 中国人寿
        private String name;
        // 总股本
        @JsonProperty("as")
        private String equity;
        // 流通股
        @JsonProperty("s")
        private String circulationShare;
    }

    public String hhqBuilder() {

        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)    // 公司代码
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName) // 公司简称
                .append(Constants.DELIMITING_COMMA.getString()).append(day)         // 收盘日
                .append(Constants.DELIMITING_COMMA.getString()).append(price)       // 收盘价
        ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }

    // 数据
    @Setter
    @Getter
    private List<HqDomain> data = new ArrayList<>();

    // SZ hhq
    @Data
    public class HqDomain implements Serializable {

        private static final long serialVersionUID = -8053707601458786497L;
        // hhq
        // 交易日期
        private String jyrq;
        // 公司代码
        private String zqdm;
        // 公司名称
        private String zqjc;
        // 前收/昨  收
        private String qss;
        // 实时股价/收盘价/今  收
        private String ss;
        // 涨幅(%)
        private String sdf;
        // 成交金额(万)
        private String cjje;
        // pe 市盈率
        private String syl1;
    }

}
