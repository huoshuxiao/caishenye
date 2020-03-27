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

        sbStr.append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getFundCode())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getFundName())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarExtendDomain.getType())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarExtendDomain.getSize())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarExtendDomain.getClosePriceDate())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarExtendDomain.getClosePrice())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarExtendDomain.getRisk())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarDetailDomain.getInceptionDate())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarExtendDomain.getReturnAvg())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn1Day())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn1Week())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn1Month())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn3Month())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn6Month())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn1Year())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn2Year())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn3Year())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn5Year())
                .append(Constants.DELIMITING_COMMA.getString()).append(morningStarBaseDomain.getReturn10Year())
                .append(Constants.DELIMITING_COMMA.getString()).append(Utils.formatNumber2String(morningStarBaseDomain.getReturnInception()));
        return sbStr.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }
}
