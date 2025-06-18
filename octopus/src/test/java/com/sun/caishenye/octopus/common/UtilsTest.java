package com.sun.caishenye.octopus.common;

import com.sun.caishenye.octopus.fund.component.CommonComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@SpringBootTest
public class UtilsTest {

    @Autowired
    private CommonComponent common;


    @Test
    void testDate() {
        long d = 1710247928283L;
        System.out.println(Utils.long2Date(d));
        System.out.println(Utils.long2DateTime(d));
        System.out.println(Utils.date2Long(Utils.long2Date(d)));
        System.out.println(Utils.dateTime2Long(LocalDateTime.now().toString()));
    }

    @Test
    void testPath() {
        String basePath = "/home/user";
        String fileName = "example.txt";

        Path path = Paths.get(basePath, fileName);
        System.out.println(path);

        System.out.println(Paths.get(common.getFilePath(), Constants.FILE_EAST_MONEY_BASE.getString()));
    }
}
