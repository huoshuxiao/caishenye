package com.sun.caishenye.rubikcube.fund.mongo.dao;

import com.sun.caishenye.rubikcube.fund.mongo.entity.FundEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFundDao extends MongoRepository<FundEntity, String> {}
