package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

// 深圳证券交易所 实时行情
@Data
public class SzHqDomain implements Serializable {

    private static final long serialVersionUID = -4856304376075694879L;

    // 时间
    private String datetime;
    // 数据
    private HqDomain data;
    // 状态消息
    private String message;
    // 实时股价
    private String price;

    @Data
    public class HqDomain {
        // 公司代码
        private String code;
        // 公司名称
        private String name;
        // 昨  收
        private String close;
        // 今  开
        private String open;
        // 实时股价
        private String now;
        // 最  高
        private String high;
        // 最  低
        private String low;
        // 成交量(手)
        private Integer volume;
        // 成交额(元)
        private Double amount;
        // 涨跌(元)
        private String delta;
        // 涨幅(%)
        private Double deltaPercent;

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
