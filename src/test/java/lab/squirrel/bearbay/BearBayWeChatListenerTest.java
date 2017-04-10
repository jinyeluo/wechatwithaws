package lab.squirrel.bearbay;

import lab.squirrel.pojo.CallbackMsg;
import lab.squirrel.pojo.CallbackMsgText;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class BearBayWeChatListenerTest {
    @Test
    public void onFollowing() throws Exception {
        BearBayWeChatListener bearBay = new BearBayWeChatListener("dummyBucket", new Properties(), null);
        CallbackMsgText msg = (CallbackMsgText) bearBay.onFollowing("id", true);
        assertEquals("id:欢迎, 我们有最年轻的海鲜", msg.getContent());
    }

}
