package com.sun.caishenye.octopus.fund.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Document(collection = "t_fund_data1")
public class FundDataMongoDomain implements Serializable {
    private String fund_code;
    private String fund_name;
    private String net_value_date;
    private String unit_net;
    private String risk;
    private String set_up_date;
    private String avg_year_profit;
    private String day_profit;
    private String week_profit;
    private String month_profit;
    private String quarter_profit;
    private String half_a_year;
    private String year_profit;
    private String two_year_profit;
    private String three_year_profit;
    private String five_year_profit;
    private String ten_year_profit;
    private String total_profit;
}
