package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 上海证券交易所 实时行情
@Data
public class ShHqDomain implements Serializable {

    private static final long serialVersionUID = -7295525306191302663L;

    // 公司代码
    private String code;
    // 昨  收
    @JsonProperty("prev_close")
    private double prevClose;
    // 最  高
    private double highest;
    // 最  低
    private double lowest;
    // 时间
    private int date;
    private int time;

    // 600000
    // 0:   20041026    收盘日
    // 1:   7.18        开盘
    // 2:   7.37        最高
    // 3:   7.13        最低
    // 4:   7.35        收盘
    // 5:   6300211     成交量(手)
    // 分时线(实时)
    private List<Number[]> line = new ArrayList<>();
    // 分时线(历史)
    private List<String[]> kline = new ArrayList<>();
    // 实时股价
    private String price;
}
