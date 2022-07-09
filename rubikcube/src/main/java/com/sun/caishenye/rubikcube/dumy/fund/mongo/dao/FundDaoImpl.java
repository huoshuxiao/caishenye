//package com.sun.caishenye.rubikcube.fund.mongo.dao;
//
//import com.sun.caishenye.rubikcube.fund.common.Constants;
//import com.sun.caishenye.rubikcube.fund.mongo.entity.FundEntity;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Slf4j
//@Repository
//public class FundDaoImpl /*implements IFundDao*/ {
//
//    private MongoTemplate mongoTemplate;
//    @Autowired
//    public void setMongoTemplate(MongoTemplate mongoTemplate) {
//        this.mongoTemplate = mongoTemplate;
//    }
//
//    public FundEntity findBy(FundEntity entity) {
////        Query query = new Query();
////        // 分页
////        query.limit(1);
////
////        // 设置条件
////        Criteria criteria = new Criteria();
////        if (StringUtils.isNotEmpty(entity.getFundCode())) {
////            criteria.and(Constants.FUND_CODE.getCode()).is(entity.getFundCode());
////        } else if (StringUtils.isNotEmpty(entity.getFundName())) {
////            criteria.and(Constants.FUND_NAME.getCode()).is(entity.getFundName());
////        }
////        query.addCriteria(criteria);
////
////        // 排序
////        Sort sort = Sort.by(Sort.Direction.DESC, "fund_code");
////        query.with(sort);
//
////        List<FundEntity> list = mongoTemplate.findAll(FundEntity.class);
////        list.forEach(t->log.debug("t {}", t));
////        log.debug("t2 {}", list.stream().findFirst().get());
////
////        FundEntity ff = mongoTemplate.findOne(new Query(Criteria.where(Constants.FUND_CODE.getCode()).is(list.stream().findFirst().get().getFund_code())), FundEntity.class);
////        log.debug("t3 {}", ff);
//
//        // 查询
//        return mongoTemplate.findById("5e6b69ffdc535e21d471ca93", FundEntity.class);
//    }
//}
