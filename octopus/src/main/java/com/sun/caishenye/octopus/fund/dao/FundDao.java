package com.sun.caishenye.octopus.fund.dao;

import com.sun.caishenye.octopus.common.Constants;
import com.sun.caishenye.octopus.common.component.CacheComponent;
import com.sun.caishenye.octopus.fund.domain.FundDomain;
import com.sun.caishenye.octopus.fund.domain.FundExtendDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class FundDao {

    @Autowired
    private CacheComponent cache;

    private String getFilePath() {
        return cache.putIfAbsentFilePath();
    }

    // 写 扩展数据
    public void writeExtendData(List<FundExtendDomain> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_FUND_EXTEND.getString()), StandardCharsets.UTF_8)) {
            for (FundExtendDomain stockDomain : data) {
                String s = stockDomain.builder() + "\r\n";
                log.debug("write fund extend data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }

    // 读 扩展数据
    public List<FundExtendDomain> readExtendData() {

        List<FundExtendDomain> list = new ArrayList<>();
        Path path = Paths.get(getFilePath() + Constants.FILE_FUND_EXTEND.getString());
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.debug("read fund extend data >> {}", line);

                FundExtendDomain baseDomain = new FundExtendDomain();
                String[] datas = line.split(Constants.DELIMITING_COMMA.getString());
                // 基金代码
                baseDomain.setFundCode(datas[0].trim());
                // 管理期间 年平均回报(%)
                baseDomain.setReturnAvg(datas[1].trim());

                list.add(baseDomain);
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }

        return list;
    }

    // 报表
    public void writeDashBoard(List<FundDomain> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFilePath() + Constants.FILE_FUND.getString()), StandardCharsets.UTF_8)) {
            for (FundDomain stockDomain : data) {
                String s = stockDomain.builder() + "\r\n";
                log.debug("write fund >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }
}
