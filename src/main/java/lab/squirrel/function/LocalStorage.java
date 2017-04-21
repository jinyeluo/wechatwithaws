package lab.squirrel.function;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public class LocalStorage implements DataStorage {
    private static final Charset UTF_8_CHARSET = java.nio.charset.StandardCharsets.UTF_8;
    private CommonFunctions helper = new CommonFunctions();

    @Override
    public String readAsStr(String bucket, String key) {
        File file = new File(bucket, key);
        try (InputStream objectData = new FileInputStream(file)) {
            return helper.inputStreamToString(objectData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(String bucket, String key, String content) {
        File file = new File(bucket, key);
        try (FileOutputStream writer = new FileOutputStream(file)) {
            writer.write(content.getBytes(UTF_8_CHARSET));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Properties readAsProperties(String bucket, String key) {
        File file = new File(bucket, key);
        Properties p = new Properties();
        try (InputStream objectData = new FileInputStream(file)) {
            p.load(objectData);
            return p;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
