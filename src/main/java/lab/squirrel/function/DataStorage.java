package lab.squirrel.function;

import java.util.Properties;

public interface DataStorage {
    String readAsStr(String bucket, String key);

    void write(String bucket, String key, String content);

    Properties readAsProperties(String bucket, String key);
}
