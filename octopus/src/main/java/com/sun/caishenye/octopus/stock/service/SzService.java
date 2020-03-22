package com.sun.caishenye.octopus.stock.service;

import com.sun.caishenye.octopus.stock.dao.SzDao;
import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 深圳证券
 */
@Slf4j
@Service
public class SzService {

    @Autowired
    private SzDao szDao;

    public List<StockDomain> readBaseData() {

        return szDao.readBaseData();
    }
}
