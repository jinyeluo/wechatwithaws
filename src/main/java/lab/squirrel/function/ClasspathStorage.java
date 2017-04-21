package lab.squirrel.function;

import java.io.IOException;
import java.util.Properties;

public class ClasspathStorage implements DataStorage {
    @Override
    public String readAsStr(String bucket, String key) {
        return null;
    }

    @Override
    public void write(String bucket, String key, String content) {
    }

    @Override
    public Properties readAsProperties(String bucket, String key) {
        Properties p = new Properties();
        try {
            p.load(getClass().getClassLoader().getResourceAsStream(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }
}
