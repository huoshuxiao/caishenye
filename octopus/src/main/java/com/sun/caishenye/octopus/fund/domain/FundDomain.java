package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.Utils;
import lombok.Data;

/**
 * 基金 总domain
 */
@Data
public class FundDomain {

    private MorningStarBaseDomain morningStarBaseDomain;

    private MorningStarDetailDomain morningStarDetailDomain;

    private MorningStarExtendDomain morningStarExtendDomain;

    public String toStr() {

        StringBuilder sbStr = new StringBuilder();

        sbStr.append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getFundCode())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getFundName())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarExtendDomain.getClosePriceDate())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarExtendDomain.getClosePrice())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarExtendDomain.getRisk())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarDetailDomain.getInceptionDate())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarExtendDomain.getReturnAvg())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Day())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Week())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Month())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn3Month())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn6Month())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Year())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn2Year())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn3Year())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn5Year())
                .append(Constants.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn10Year())
                .append(Constants.DELIMITING_COMMA.getCode()).append(Utils.formatNumber2String(morningStarBaseDomain.getReturnInception()));
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getCode(), "");
    }
}
