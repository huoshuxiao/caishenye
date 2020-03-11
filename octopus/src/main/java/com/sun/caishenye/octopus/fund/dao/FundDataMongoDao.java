//package com.sun.caishenye.octopus.fund.dao;
//
//import com.sun.caishenye.octopus.fund.domain.FundDataMongoDomain;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * @author Administrator
// */
//@Component
//public class FundDataMongoDao {
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    /**
//     * 新增信息
//     *
//     * @param FundDataMongoEntity
//     */
//    public void save(FundDataMongoDomain fund) {
//        mongoTemplate.save(fund);
//    }
//
//    /**
//     * 修改信息
//     *
//     * @param FundDataMongoEntity
//     */
//    public void update(FundDataMongoDomain fund) {
//        //修改的条件
//        Query query = new Query(Criteria.where("fund_code").is(fund.getFund_code()));
//
//        //修改的内容
//        Update update = new Update();
//        update.set("name", fund.getFund_name());
//
//        mongoTemplate.updateFirst(query, update, FundDataMongoDomain.class);
//    }
//
//    /**
//     * 查询所有信息
//     *
//     * @return
//     */
//    public List<FundDataMongoDomain> findAll() {
//        return mongoTemplate.findAll(FundDataMongoDomain.class);
//    }
//
//    /**
//     * 根据id查询所有信息
//     *
//     * @param id
//     */
//    public void delete(String fundCode) {
//        FundDataMongoDomain byId = mongoTemplate.findById(fundCode, FundDataMongoDomain.class);
//        mongoTemplate.remove(byId);
//    }
//
//    /**
//     * 根据id查询所有信息
//     *
//     * @param fundCode
//     */
//    public FundDataMongoDomain findById(String fundCode) {
//        return mongoTemplate.findById(fundCode, FundDataMongoDomain.class);
//
//    }
//
//
//}
