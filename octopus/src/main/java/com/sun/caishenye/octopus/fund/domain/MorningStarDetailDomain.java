package com.sun.caishenye.octopus.fund.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * http://cn.morningstar.com/handler/quicktake.ashx?command=manage&fcid=0P0001696E&randomid=0.7770353061521689
 * 返回值 Bean
 */
@Data
public class MorningStarDetailDomain implements Serializable {

    /* 基金管理 */
    // 成立日期
    @JsonProperty("InceptionDate")
    private String inceptionDate;
    // 投资目标
    @JsonProperty("Profile")
    private String profile;
    // 托管银行
    @JsonProperty("CustodianName")
    private String custodianName;
    // 托管银行网站
    @JsonProperty("CustodianHomepage")
    private String custodianHomepage;
    // 基金管理公司
    @JsonProperty("CompanyName")
    private String companyName;
    // 基金管理公司网站
    @JsonProperty("CompanyHomepage")
    private String companyHomepage;
    // 基金管理公司电话
    @JsonProperty("Tel")
    private String tel;
    // 基金管理公司传真
    @JsonProperty("Fax")
    private String fax;
    // 基金管理公司地址
    @JsonProperty("Address")
    private String address;

    /* 基金经理 */
    @JsonProperty("Managers")
    private List<MorningStarDetailManagerDomain> managers = new ArrayList<>();
}
