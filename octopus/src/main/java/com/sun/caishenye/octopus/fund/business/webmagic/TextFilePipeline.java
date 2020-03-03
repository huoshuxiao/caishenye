package com.sun.caishenye.octopus.fund.business.webmagic;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.annotation.ThreadSafe;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@Slf4j
@ThreadSafe
public class TextFilePipeline extends FilePersistentBase implements Pipeline {

    @Setter
    private String file;

    private Path path;

    private void setPrintWrite() {

        try {
            String _path = super.path + PATH_SEPERATOR + file;
            path = Paths.get(_path);

            Files.deleteIfExists(path);
//            path = Paths.get(_path);

        } catch (IOException e) {
            log.error("delete file error", e);
        }
    }

    public TextFilePipeline(String path, String file) {
        setPath(path);
        setFile(file);
        setPrintWrite();
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                byte data[] = (entry.getValue().toString()+"\r\n").getBytes();
                out.write(data, 0, data.length);

            }
        } catch (IOException e) {
            log.error("write file error", e);
        }
    }
}
