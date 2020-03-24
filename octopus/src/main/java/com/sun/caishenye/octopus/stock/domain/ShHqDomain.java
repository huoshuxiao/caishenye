package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 上海证券交易所 实时行情
@Data
public class ShHqDomain {

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
    // 分时线
    private List<Number[]> line = new ArrayList<>();
    // 实时股价
    private String price;
}
