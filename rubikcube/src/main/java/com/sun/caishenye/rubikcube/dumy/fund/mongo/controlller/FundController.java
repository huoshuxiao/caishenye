package com.sun.caishenye.rubikcube.dumy.fund.mongo.controlller;

import com.sun.caishenye.rubikcube.dumy.fund.mongo.entity.FundEntity;
import com.sun.caishenye.rubikcube.dumy.fund.mongo.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("fund/")
public class FundController {

    private FundService fundService;
    @Autowired
    public void setFundService(FundService fundService) {
        this.fundService = fundService;
    }

    // 批量添加/更新数据
    @PostMapping("batch")
    public List<FundEntity> insert(List<FundEntity> list) {
        return fundService.save(list);
    }

    // 单条添加/更新数据
    @PostMapping("save")
    public FundEntity insert(FundEntity fundEntity) {
        return fundService.save(fundEntity);
    }

    @GetMapping("list")
    public List<FundEntity> list(FundEntity entity) {
        return fundService.list(entity);
    }

    @GetMapping("get")
    public FundEntity findOne(FundEntity entity) {
        return fundService.get(entity);
    }
}
