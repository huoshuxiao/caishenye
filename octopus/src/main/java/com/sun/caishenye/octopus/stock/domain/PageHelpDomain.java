package com.sun.caishenye.octopus.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageHelpDomain {

    private Integer beginPage;
    private Integer cacheSize;
    private Integer endPage;
    private Integer pageCount;
    private Integer pageNo;
    private Integer pageSize;
    private Integer total;

    @JsonProperty("data")
    private List<StockDomain> stockDomain;
}
