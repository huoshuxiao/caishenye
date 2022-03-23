package com.sun.caishenye.octopus.fund.service;

import com.sun.caishenye.octopus.common.Utils;
import com.sun.caishenye.octopus.fund.dao.FundDao;
import com.sun.caishenye.octopus.fund.domain.EastMoneyBaseDomain;
import com.sun.caishenye.octopus.fund.domain.EastMoneyDetailDomain;
import com.sun.caishenye.octopus.fund.domain.FundDomain;
import com.sun.caishenye.octopus.fund.domain.FundExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FundService {

//    private MorningStarService morningStarService;
    private EastMoneyService eastMoneyService;

    private FundDao fundDao;

    private static final Double BASE_DAY = 360d;

    @Autowired
    public FundService(/*MorningStarService morningStarService, */EastMoneyService eastMoneyService, FundDao fundDao) {
//        this.morningStarService = morningStarService;
        this.eastMoneyService = eastMoneyService;
        this.fundDao = fundDao;
    }

    public void run() {
        eastMoneyService.base();
//        morningStarService.base();
//        morningStarService.extend();
        eastMoneyService.detail();
//        morningStarService.detail();
        // 自定义指标
        customer();

        fund();
    }

    public Object fund() {

        List<EastMoneyBaseDomain> baseDataList = eastMoneyService.readBaseData().stream().sorted(
                Comparator.comparing(EastMoneyBaseDomain::getFundCode)).collect(Collectors.toList());
        List<EastMoneyDetailDomain> detailDataList = eastMoneyService.readDetailData().stream().sorted(
                Comparator.comparing(EastMoneyDetailDomain::getFundCode)).collect(Collectors.toList());
        List<FundExtendDomain> extendDomainList = fundDao.readExtendData().stream().sorted(
                Comparator.comparing(FundExtendDomain::getFundCode)).collect(Collectors.toList());

        List<FundDomain> fundDomainList = new ArrayList<>(baseDataList.size());
        for (int i = 0; i < baseDataList.size(); i++) {
            FundDomain fundDomain = new FundDomain(baseDataList.get(i), detailDataList.get(i), extendDomainList.get(i));
            fundDomainList.add(fundDomain);
        }

        fundDao.writeDashBoard(fundDomainList);

        return "finished";
    }

    // 自定义指标
    public Object customer() {

        List<EastMoneyDetailDomain> detailDataList = eastMoneyService.readDetailData();
        List<FundExtendDomain> fundDomainList = new ArrayList<>(detailDataList.size());

        for (EastMoneyDetailDomain detailDomain: detailDataList) {

            FundExtendDomain fundDomain = new FundExtendDomain();
            // 计算 管理期间 年平均回报(%)
            String returnAvg = calReturnAvg(detailDomain);
            fundDomain.setReturnAvg(returnAvg);

            fundDomain.setFundCode(detailDomain.getFundCode());

            fundDomainList.add(fundDomain);
        }

        fundDao.writeExtendData(fundDomainList);

        return "finished";
    }

    // 计算 管理期间 年平均回报(%)
    private String calReturnAvg(EastMoneyDetailDomain detailDomain) {

        log.debug("calReturnAvg data {}", detailDomain.toString());

        // 管理回报(%)
        String returnInception = Utils.formatNumber2String(detailDomain.getManagementReturn()).replace("%", "");
        // 管理期间
        String inceptionDate = detailDomain.getManagementRange().replace("~至今", "");

        if ("--".equals(returnInception) || "-".equals(returnInception) || StringUtils.isEmpty(returnInception) || StringUtils.isEmpty(inceptionDate)) {
            return "-";
        }

        // 年平均回报(%) = 管理以来(%) / (当前日期 - 管理日期)
        LocalDate today = LocalDate.now();
        LocalDate inceptionLocalDate = LocalDate.of(Integer.parseInt(inceptionDate.substring(0, 4)),
                Integer.parseInt(inceptionDate.substring(5, 7)),
                Integer.parseInt(inceptionDate.substring(8, 10)));
        double returnAvg = Double.parseDouble(returnInception) / (ChronoUnit.DAYS.between(inceptionLocalDate, today) / BASE_DAY);
        return Utils.formatNumber2String(String.format("%.2f", returnAvg));
    }
}
