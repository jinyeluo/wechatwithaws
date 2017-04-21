package lab.squirrel.function;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.*;

public class LocalStorageTest {
    @Ignore // depends on OS, the test below might fail, hence ignored
    @Test
    public void testReadWrite() throws Exception {
        LocalStorage localStorage = new LocalStorage();
        String bucket = "/temp";
        String key = "test.txt";
        localStorage.write(bucket, key, "a=b");
        Properties properties = localStorage.readAsProperties(bucket, key);
        Assert.assertEquals("b", properties.getProperty("a"));
        new File(bucket, key).delete();
    }

}
