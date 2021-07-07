//package com.sun.caishenye.octopus.fund.domain;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Data;
//
//import java.io.Serializable;
//
///**
// * http://cn.morningstar.com/handler/quicktake.ashx?command=manage&fcid=0P0001696E&randomid=0.7770353061521689
// * 返回值 Managers Bean
// */
//@Data
//public class MorningStarDetailManagerDomain implements Serializable {
//
//    private static final long serialVersionUID = -1016374836486130207L;
//
//    // 基金经理
//
//    // 是否离任, false: 否, true：是
//    @JsonProperty("Leave")
//    private Boolean leave;
//    // 管理期间
//    @JsonProperty("ManagementRange")
//    private String managementRange;
//    // 管理时间
//    @JsonProperty("ManagementTime")
//    private String managementTime;
//    // 管理时间
//    @JsonProperty("StartDate")
//    private String startDate;
//    // 管理时间
//    @JsonProperty("EndDate")
//    private String endDate;
//    // 基金经理ID
//    @JsonProperty("ManagerId")
//    private String managerId;
//    // 基金经理Name
//    @JsonProperty("ManagerName")
//    private String managerName;
//    // 基金经理简历
//    @JsonProperty("Resume")
//    private String resume;
//}
