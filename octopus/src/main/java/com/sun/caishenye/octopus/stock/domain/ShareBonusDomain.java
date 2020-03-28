package com.sun.caishenye.octopus.stock.domain;

import lombok.Data;

import java.io.Serializable;

// 分红配股
@Data
public class ShareBonusDomain  implements Serializable {

    private static final long serialVersionUID = -6750598900254608557L;

    // 公告日期
    private String bonusDate;
    // 送股(股)
    private String bonus;
    // 转增(股)
    private String increase;
    // 派息(税前)(元)
    private String dividend;
    // 进度
    private String schedule;
    // 除权除息日 fmt: 2016-06-03
    private String dividendDate;
    // 股权登记日 fmt: 2016-06-03
    private String registrationDate;
    // 红股上市日 fmt: 2016-06-03
    private String listingDate;

}
