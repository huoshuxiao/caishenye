package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 基金 总domain
 */
@Setter
@Getter
public class FundDomain implements Serializable {

    private static final long serialVersionUID = 5296550176209015089L;

    private FundDomain() {}

    public FundDomain(EastMoneyBaseDomain baseDomain, EastMoneyDetailDomain detailDomain, FundExtendDomain extendDomain) {
        this.baseDomain = baseDomain;
        this.detailDomain = detailDomain;
        this.extendDomain = extendDomain;
    }

//    private MorningStarBaseDomain baseDomain;
//    private MorningStarDetailDomain morningStarDetailDomain;
//    private MorningStarExtendDomain detailDomain;
    
    private EastMoneyBaseDomain baseDomain;
    private EastMoneyDetailDomain detailDomain;
    private FundExtendDomain extendDomain;

    public String builder() {

        String sbStr = Constants.DELIMITING_COMMA.getString() + baseDomain.getFundCode() +  // 基金代码
                Constants.DELIMITING_COMMA.getString() + baseDomain.getFundName() +         // 基金名称
                Constants.DELIMITING_COMMA.getString() + detailDomain.getType() +           // 基金类型
                Constants.DELIMITING_COMMA.getString() + detailDomain.getSize() +           // 基金规模
                Constants.DELIMITING_COMMA.getString() + baseDomain.getInceptionDate() +    // 成立日期
                Constants.DELIMITING_COMMA.getString() + detailDomain.getManagerName() +    // 基金经理
                Constants.DELIMITING_COMMA.getString() + detailDomain.getManagementRange() + // 管理期间
                Constants.DELIMITING_COMMA.getString() + detailDomain.getManagementTime() +  // 管理时间
                Constants.DELIMITING_COMMA.getString() + format(detailDomain.getManagementReturn()) + // 管理回报(%)
                Constants.DELIMITING_COMMA.getString() + format(extendDomain.getReturnAvg()) +      // 管理期间 年平均回报(%)
                Constants.DELIMITING_COMMA.getString() + detailDomain.getRisk() +                   // 风险
                Constants.DELIMITING_COMMA.getString() + detailDomain.getClosePriceDate() +         // 净值日期
                Constants.DELIMITING_COMMA.getString() + format(detailDomain.getClosePrice()) +     // 单位净值
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturnThisYear()) +   // 今年以来(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn1Day()) +       // 1天回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn1Week()) +      // 1周回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn1Month()) +     // 1个月回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn3Month()) +     // 3个月回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn6Month()) +     // 6个月回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn1Year()) +      // 1年回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn2Year()) +      // 2年回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn3Year()) +      // 3年回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn5Year()) +      // 5年回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturn10Year()) +     // 10年回报(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getReturnInception()) +  // 设立以来(%)
                Constants.DELIMITING_COMMA.getString() + format(baseDomain.getTotalPrice());        // 累计净值;
        return sbStr.replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }

    private String format(String data) {
        if (StringUtils.isEmpty(data) || "--".equals(data) || "-".equals(data)) {
            return "-";
        }
        return Utils.formatNumber2String(String.format("%.2f", Double.parseDouble(data)));
    }
}
