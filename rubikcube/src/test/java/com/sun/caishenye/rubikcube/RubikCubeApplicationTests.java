package com.sun.caishenye.rubikcube;

import com.sun.caishenye.rubikcube.fund.mongo.entity.FundEntity;
import com.sun.caishenye.rubikcube.fund.mongo.service.FundService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class RubikCubeApplicationTests {

    @Autowired
    private FundService fundService;

    @Test
    void contextLoads() {
    }

    @Test
    void testMongoAdd() {
        FundEntity entity = buildData();
        fundService.save(entity);
    }

    @Test
    void testMongoList() {
        FundEntity entity = new FundEntity();
//        entity.setFundCode("163402");
        List<FundEntity> entities = fundService.list(entity);
        entities.forEach(t-> log.debug("data :: {}", t));
    }

    @Test
    void testMongoGet() {
        FundEntity entity = new FundEntity();
        entity.setFundCode("163402");
        entity = fundService.get(entity);
        log.debug("data :: {}", entity);
    }

    private FundEntity buildData() {
        FundEntity entity = new FundEntity();
        entity.setFundCode("163402");
        entity.setFundName("兴全趋势投资混合(LOF)");
        entity.setClosePriceDate("2020/3/3");
        entity.setClosePrice("0.8201");
        entity.setRisk("FALSE");
        entity.setInceptionDate("2005/11/3");
        entity.setReturnAvg("125.93");
        entity.setReturn1Day("0.13");
        entity.setReturn1Week("-0.12");
        entity.setReturn1Month("16.36");
        entity.setReturn3Month("19.29");
        entity.setReturn6Month("21.45");
        entity.setReturn1Year("31.04");
        entity.setReturn2Year("12.97");
        entity.setReturn3Year("16.44");
        entity.setReturn5Year("15.89");
        entity.setReturn10Year("11.58");
        entity.setReturnInception("1831.21");
        return entity;
    }

}
