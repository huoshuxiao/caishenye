package com.sun.caishenye.octopus.stock.dao;

import com.sun.caishenye.octopus.stock.domain.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Repository
public class StockDao {

    protected final String BASE_DATA_FILE_NAME = "data/HQ.csv";

    public void writeHqData(List<StockDomain> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(BASE_DATA_FILE_NAME), StandardCharsets.UTF_8)) {
            for (StockDomain stockDomain : data) {
                String s = stockDomain.toHqStr() + "\r\n";
                log.debug("write hq data >> {}", s);
                writer.write(s, 0, s.length());
            }
        } catch (IOException x) {
            log.error(String.format("IOException: %s%n", x));
        }
    }
}
