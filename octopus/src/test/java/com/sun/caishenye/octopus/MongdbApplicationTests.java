//package com.sun.caishenye.octopus;
//
//import com.sun.caishenye.octopus.fund.dao.FundDataMongoDao;
//import com.sun.caishenye.octopus.fund.domain.FundDataMongoDomain;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
//@SpringBootTest
//public class MongdbApplicationTests {
//    @Autowired
//    private FundDataMongoDao fundDataMongoDao;
//
//    @Test
//    public void mytest(){
//        this.save();
//        this.findAll();
//    }
//
//
//    public void findAll() {
//        List<FundDataMongoDomain> all = fundDataMongoDao.findAll();
//        System.out.println(all.size());
//    }
//
//
//    public void save() {
//        FundDataMongoDomain fundDataMongoDomain = new FundDataMongoDomain();
//        fundDataMongoDomain.setFund_code("999999");
//        fundDataMongoDomain.setFund_name("金牌基金");
//        fundDataMongoDao.save(fundDataMongoDomain);
//    }
//
//
//    public void update() {
//        FundDataMongoDomain fundDataMongoDomain = new FundDataMongoDomain();
//        fundDataMongoDomain.setFund_code("999999");
//        fundDataMongoDomain.setFund_name("银牌基金");
//        fundDataMongoDao.update(fundDataMongoDomain);
//    }
//
//
//    public void delete() {
//        fundDataMongoDao.delete("61");
//    }
//}
