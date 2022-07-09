package com.sun.caishenye.rubikcube.dumy.fund.mongo.entity;

import com.sun.caishenye.rubikcube.dumy.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("fund_t")
public class FundEntity extends BaseEntity {

    // 基金代码
    @Id         // 影响 findOne 的查询结果，没有id是不用id注解
    @Field("fund_code")
    protected String fundCode;

    // 基金名称
    @Field("fund_name")
    protected String fundName;

    // 净值日期
    @Field("close_price_date")
    private String closePriceDate;

    // 单位净值
    @Field("close_price")
    private String closePrice;

    // 风险
    private String risk;

    // 成立日期
    @Field("inception_date")
    private String inceptionDate;

    // 年平均回报(%)
    @Field("return_avg")
    private String returnAvg;

    // 1天回报(%)
    @Field("return_1day")
    protected String return1Day;

    // 1周回报(%)
    @Field("return_1week")
    protected String return1Week;

    // 1个月回报(%)
    @Field("return_1month")
    protected String return1Month;

    // 3个月回报(%)
    @Field("return_3month")
    protected String return3Month;

    // 6个月回报(%)
    @Field("return_6month")
    protected String return6Month;

    // 1年回报(%)
    @Field("return_1year")
    protected String return1Year;

    // 2年回报(%)
    @Field("return_2year")
    protected String return2Year;

    // 3年回报(%)
    @Field("return_3year")
    protected String return3Year;

    // 5年回报(%)
    @Field("return_5year")
    protected String return5Year;

    // 10年回报(%)
    @Field("return_10year")
    protected String return10Year;

    // 设立以来(%)
    @Field("return_inception")
    protected String returnInception;
}
