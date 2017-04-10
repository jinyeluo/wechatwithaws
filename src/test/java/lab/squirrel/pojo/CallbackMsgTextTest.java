package lab.squirrel.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CallbackMsgTextTest {

    private static final long INT_12345 = 12345;

    @Test
    public void testToString() throws JsonProcessingException {
        CallbackMsgText msg = new CallbackMsgText();
        msg.setContent("content");
        msg.setCreateTime(INT_12345);
        msg.setFromUserName("from");
        msg.setToUserName("to");
        XmlMapper xmlMapper = new XmlMapper();
        String expected = "<xml><MsgType><![CDATA[text]]></MsgType>" +
            "<ToUserName><![CDATA[to]]></ToUserName>" +
            "<FromUserName><![CDATA[from]]></FromUserName>" +
            "<CreateTime>12345</CreateTime>" +
            "<Content><![CDATA[content]]></Content></xml>";
        assertEquals(expected, xmlMapper.writeValueAsString(msg));

        msg.setContent("user123:" + "欢迎，我们有最年轻的海鲜");
        String chinese = xmlMapper.writeValueAsString(msg);
        assertTrue(chinese.contains("欢迎，我们有最年轻的海鲜"));

        byte[] bytes = chinese.getBytes(StandardCharsets.UTF_8);
        String newChinese = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(newChinese.contains("欢迎，我们有最年轻的海鲜"));
    }
}
