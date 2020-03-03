package com.sun.caishenye.octopus.fund.domain;

import com.sun.caishenye.octopus.common.Constans;
import lombok.Data;

/**
 * 晨星 总domain
 */
@Data
public class MorningStarDomain {

    private MorningStarBaseDomain morningStarBaseDomain;

    private MorningStarDetailDomain morningStarDetailDomain;

    private MorningStarExtendDomain morningStarExtendDomain;

    public String toStr() {

        StringBuilder sbStr = new StringBuilder();

        sbStr.append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getFundCode())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getFundName())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarDetailDomain.getInceptionDate())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarExtendDomain.getReturnAvg())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Day())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Week())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Month())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn3Month())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn6Month())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn1Year())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn2Year())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn3Year())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn5Year())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturn10Year())
                .append(Constans.DELIMITING_COMMA.getCode()).append(morningStarBaseDomain.getReturnInception());
        return sbStr.toString().replaceFirst(Constans.DELIMITING_COMMA.getCode(), "");
    }
}
