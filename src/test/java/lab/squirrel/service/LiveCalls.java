package lab.squirrel.service;

import com.amazonaws.services.s3.AmazonS3;
import lab.squirrel.function.CommonFunctions;
import lab.squirrel.function.S3Functions;
import lab.squirrel.function.WeChatFunctions;
import lab.squirrel.pojo.AccessTokenWeChatResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LiveCalls {
    private WeChatFunctions weChatFunctions;


    @Before
    public void setup() throws IOException {
        Properties p = new Properties();
        p.load(getClass().getClassLoader().getResourceAsStream("data/app.properties"));
        weChatFunctions = new MyWeChatFunctions(p);
    }

    @Ignore
    @Test
    public void testGetToken() throws IOException {
        AccessTokenWeChatResponse accessToken = weChatFunctions.getTokenRemote(null);
        System.out.println(accessToken);
    }

    @Ignore
    @Test
    public void testSetMenu() throws IOException {
        String accessToken = weChatFunctions.getAccessToken(null);
        System.out.println(accessToken);

        CommonFunctions commonFunctions = new CommonFunctions();
//        String token = "96fRa8NBQNeTGNCx2TQL_ip725PZw_DEm9zgil24Au7ntwLax3bBGcmX3TrsSJLSIU6zMizDl2YNrCqgVQm8FvA0" +
//            "z0cC7eaRwxxVRDhFhSyXS-yj6_3d-Z548E5LvHOBGXSdAIAJAV";
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        String menuStr =
            commonFunctions.inputStreamToString(getClass().getClassLoader()
                .getResourceAsStream("sample/menu.json"));
        String menuResp = commonFunctions.httpPostCall("https", "api.wechat.com",
            "/cgi-bin/menu/create", params, menuStr);
        System.out.println(menuResp);
    }

    private class MyWeChatFunctions extends WeChatFunctions {
        public MyWeChatFunctions(Properties p) {
            super("dummyBucket", p, null);
            setS3FunctionForTesting(new MyS3Function());
        }
    }

    private class MyS3Function extends S3Functions {
        public MyS3Function() {
            super(null);
        }

        @Override
        public Properties readS3ObjAsProperties(String s3bucket, String key) {
            return new Properties();
        }

        @Override
        public void writeToS3Obj(String bucket, String key, String content) {
        }
    }
}