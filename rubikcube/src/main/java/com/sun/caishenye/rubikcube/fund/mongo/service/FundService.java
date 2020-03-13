package com.sun.caishenye.rubikcube.fund.mongo.service;

import com.sun.caishenye.rubikcube.fund.mongo.dao.IFundDao;
import com.sun.caishenye.rubikcube.fund.mongo.entity.FundEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundService {

    private IFundDao iFundDao;
    @Autowired
    public void setIFundDao(IFundDao iFundDao) {
        this.iFundDao = iFundDao;
    }

//    @Transactional(rollbackFor = Exception.class)
    public List<FundEntity> save(List<FundEntity> list) {
        iFundDao.deleteAll();
        return iFundDao.saveAll(list);
    }

//    @Transactional(rollbackFor = Exception.class)
    public FundEntity save(FundEntity fundEntity) {
        return iFundDao.save(fundEntity);
    }

    public List<FundEntity> list(FundEntity entity) {
        Example example = Example.of(entity);
        return iFundDao.findAll(example);
    }

    public FundEntity get(FundEntity entity) {

        Example<FundEntity> example = Example.of(entity);
        return iFundDao.findOne(example).orElse(entity);
    }
}
