package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.caishenye.octopus.common.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

// 历史行情 - 日K
public class DayLineDomain implements Serializable {

    private static final long serialVersionUID = 4371636697825834191L;

    @Setter
    @Getter
//    @JsonProperty("summary")
    private BaseDomain summary;
    // 600000
    //0	:	20200325
    //1	:	10.06
    //2	:	10.15
    //3	:	10.2
    //4	:	10.27
    //5	:	10.12
    //6	:	321723
    //7	:	327768574
    //8	:		false
    @Setter
    @Getter
    private List<String[]> hqs;

//    @Setter
//    @Getter
////    @JsonProperty("hqs")
//    private List<HqDomain> hqDomainList;

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
    public class BaseDomain {
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

//    @Data
//    public class HqDomain {
//        // 昨  收
//        private String close;
//        // 今  开
//        private String open;
//        // 实时股价
//        private String now;
//        // 最  高
//        private String high;
//        // 最  低
//        private String low;
//        // 成交量(手)
//        private Integer volume;
//        // 成交额(元)
//        private Double amount;
//        // 涨跌(元)
//        private String delta;
//        // 涨幅(%)
//        private Double deltaPercent;
//        // 收盘日
//        private String day;
//
//
//    }

    public String hhqBuilder() {

        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)    // 公司代码
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName) // 公司简称
                .append(Constants.DELIMITING_COMMA.getString()).append(day)         // 收盘日
                .append(Constants.DELIMITING_COMMA.getString()).append(price)       // 收盘价
        ;
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
