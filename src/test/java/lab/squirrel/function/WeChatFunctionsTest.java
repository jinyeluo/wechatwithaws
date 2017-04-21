package lab.squirrel.function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class WeChatFunctionsTest {

    private WeChatFunctions weChatFunctions;


    @Before
    public void setup() throws IOException {
        weChatFunctions = new WeChatFunctions("dummy", new ClasspathStorage());
    }

    @Test
    public void testSha1() {
        Assert.assertEquals("d0be2dc421be4fcd0172e5afceea3970e2f3d940",
            weChatFunctions.sha1("apple"));
    }

    /**
     * signature=1545794b584d281e5ff2bf3ad7e046365c31f422&echostr=7302992831166924722&timestamp=1491715890
     * &nonce=760936520
     */
    @Test
    public void testMessageAuthentication() {
        HashMap<String, String[]> querystring = new HashMap<>();
        querystring.put("signature", new String[] {"1545794b584d281e5ff2bf3ad7e046365c31f422"});
        querystring.put("echostr", new String[] {"7302992831166924722"});
        querystring.put("timestamp", new String[] {"1491715890"});
        querystring.put("nonce", new String[] {"760936520"});
        String answer = weChatFunctions.messageAuthentication(querystring, null);
        Assert.assertEquals(querystring.get("echostr")[0], answer);
    }
}

