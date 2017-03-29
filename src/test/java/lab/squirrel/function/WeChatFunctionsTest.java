package lab.squirrel.function;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WeChatFunctionsTest {
    @Ignore
    @Test
    public void testSetMenu() throws IOException {
        Properties p = new Properties();
        p.load(getClass().getClassLoader().getResourceAsStream("data/appid.properties"));
        WeChatFunctions weChatFunctions = new WeChatFunctions();
        weChatFunctions.setConfig(p);
        String accessToken = weChatFunctions.getAccessToken();
        System.out.println(accessToken);

        CommonFunctions commonFunctions = new CommonFunctions();
        Map<String, Object> stringObjectMap = commonFunctions.jsonToMap(accessToken);
        String token = String.valueOf(stringObjectMap.get("access_token"));
//        String token = "96fRa8NBQNeTGNCx2TQL_ip725PZw_DEm9zgil24Au7ntwLax3bBGcmX3TrsSJLSIU6zMizDl2YNrCqgVQm8FvA0" +
//            "z0cC7eaRwxxVRDhFhSyXS-yj6_3d-Z548E5LvHOBGXSdAIAJAV";
        Map<String, String> params = new HashMap<>();
        params.put("access_token", token);
        String menuStr =
            commonFunctions.inputStreamToString(getClass().getClassLoader()
                .getResourceAsStream("sample/menu.json"));
        String menuResp = commonFunctions.httpPostCall("https", "api.wechat.com", "/cgi-bin/menu/create", params, menuStr);
        System.out.println(menuResp);
    }

    @Test
    public void testSha1() {
        Assert.assertEquals("d0be2dc421be4fcd0172e5afceea3970e2f3d940",
            new WeChatFunctions().sha1("apple"));
    }

}

