package com.sun.caishenye.octopus.stock.domain;

import com.sun.caishenye.octopus.common.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资金流
 */
@ToString
public class MoneyFlowDomain {

    // 公司代码
    @Setter
    @Getter
    private String companyCode;

    // 公司简称
    @Setter
    @Getter
    private String companyName;

//    @Setter
//    @Getter
//    private BaseDomain summary = new BaseDomain();
//    @Data
//    public class BaseDomain implements Serializable {
//        // sh601628
//        private String id;
//        // 601628
//        private String code;
//        // 中国人寿
//        private String name;
//        // 总股本
//        @JsonProperty("as")
//        private String equity;
//        // 流通股
//        @JsonProperty("s")
//        private String circulationShare;
//    }

    // 东方财富网
    //0 :   2024-07-12        收盘日
    //1 :   -53552368.0       主力净流入
    //2 :   127562864.0       小单净流入
    //3 :   -74010480.0       中单净流入
    //4 :   -143662528.0      大单净流入
    //5 :   90110160.0        超大单净流入
    //6 :   -1.95             主力净流入   占比
    //7 :   4.64              小单净流入   占比
    //8 :   -2.69             中单净流入   占比
    //9 :   -5.23             大单净流入   占比
    //10:   3.28              超大单净流入 占比
    //11:   25.60             收盘价
    //12:   -1.46             涨跌幅
    //13:   0.00
    //14:   0.00
    @Setter
    @Getter
    private List<String> klines = new ArrayList<>();

    public String stockBuilder() {
        StringBuilder sbStr = new StringBuilder();
        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)    // 公司代码
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName) // 公司简称
        ;
        klines.forEach(t -> {
            String[] k = t.split("Constants.DELIMITING_COMMA");
            Arrays.stream(k).forEach(a -> {
                sbStr.append(Constants.DELIMITING_COMMA.getString()).append(a);
            });
        });
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
