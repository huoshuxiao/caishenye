//package com.sun.caishenye.rubikcube.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.MongoTransactionManager;
//
///**
// * 1.MongoDB的版本必须是4.0
// * 2.MongoDB事务功能必须是在多副本集的情况下才能使用，否则报错"Sessions are not supported by the MongoDB cluster to which this client is connected"，4.2版本会支持分片事务。
// * 3.事务控制只能用在已存在的集合中，也就是集合需要手工添加不会由jpa创建会报错"Cannot create namespace glcloud.test_user in multi-document transaction."
// * 4.多数据源时需要指定事务 @Transactional(value = "transactionManager") 如果只有1个数据源不需要指定value
// * 5.事务注解到类上时，该类的所有 public 方法将都具有该类型的事务属性，但一般都是注解到方法上便于实现更精确的事务控制
// * 6.事务传递性，事务子方法上不必添加事务注解，如果子方法也提供api调用可用注解propagation = Propagation.REQUIRED也就是继承调用它的事务，如果没有事务则新起一个事务
// *
// */
//@Configuration
//public class MongoConfig {
//
//    @Bean
//    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }
//
//}
//
