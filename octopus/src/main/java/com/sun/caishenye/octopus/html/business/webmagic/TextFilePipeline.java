package com.sun.caishenye.octopus.html.business.webmagic;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.annotation.ThreadSafe;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.FilePipeline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@ThreadSafe
public class TextFilePipeline extends FilePipeline {

    @Setter
    private String file;

    public TextFilePipeline() {
        setPath("/data/webmagic/");
        setFile("webmagic.log");
    }

    public TextFilePipeline(String path, String file) {
        setPath(path);
        setFile(file);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        String path = this.path + PATH_SEPERATOR;
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(path + file)),"UTF-8"));
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
//                    printWriter.println(entry.getKey() + ":");
                    printWriter.println(entry.getValue() + ",");
                    for (Object o : value) {
                        printWriter.println(o);
                    }
                } else {
//                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                    printWriter.println(entry.getValue());
                }
            }
            printWriter.close();
        } catch (IOException e) {
            log.error("write file error", e);
        }
    }
}
