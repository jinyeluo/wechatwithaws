package lab.squirrel.function;

import lab.squirrel.pojo.AccessToken;
import lab.squirrel.pojo.XmlMsg;
import lab.squirrel.pojo.InComingMsgText;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommonFunctionsTest {
    private static final int EXPECTED_7200 = 7200;
    private CommonFunctions commonFunctions;

    @Before
    public void setUp() throws Exception {
        commonFunctions = new CommonFunctions();
    }

    @Ignore
    @Test
    public void testHttpPostCall() throws Exception {

        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credential");
        params.put("appid", "wxdc6d69fef9b6a0e3");
        params.put("secret", "incorrectstring05aee1fe1fb467c10");
        System.out.println(commonFunctions.httpGetCall("https", "api.wechat.com",
            "/cgi-bin/token", params));
    }

    @Test
    public void testJsonToMap() {
        String input =
            "{\"access_token\":\"L_q-2-b2lr7MosUJ_k34DCHwAVZOEXH2VULzv9xoShHKKaS3rG6Uxs3TArEZ2UwW1uc7SxXdpnrhAnfECBScCvNE_NOI7fsm1UJziNo8MLAgp-C2VtWQPoz9ZJcQihTzCHCjADAJOJ\",\"expires_in\":7200}\n";
        Map<String, Object> map = commonFunctions.jsonToMap(input);
        Assert.assertEquals(EXPECTED_7200, map.get("expires_in"));
    }

    @Test
    public void testConvertJson() {

        String json = commonFunctions.objectToJson(new AccessToken());
        Assert.assertEquals("{\"expires_at\":0}", json);

        AccessToken obj2 = new AccessToken();
        obj2.setAccess_token("token");
        obj2.setExpires_at(1000);
        String json2 = commonFunctions.objectToJson(obj2);
        Assert.assertEquals("{\"expires_at\":1000,\"access_token\":\"token\"}", json2);
    }

    @Test
    public void testXmlWrite() throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        XmlMsg msg = new XmlMsg();
        String result = mapper.writeValueAsString(msg);
        Assert.assertEquals("<xml/>", result);

        msg.setMsgType("request");
        result = mapper.writeValueAsString(msg);
        Assert.assertEquals("<xml><MsgType><![CDATA[request]]></MsgType></xml>", result);

    }

    @Test
    public void testXmlReader() throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        InComingMsgText in = mapper.readValue(
            mapper.getFactory().createParser(getClass().getResourceAsStream("/sample/in_comming_msg.xml")),
            InComingMsgText.class);

        Assert.assertEquals("this is a test", in.getContent());
        Assert.assertEquals(1348831860, in.getCreateTime().longValue());
        Assert.assertEquals("1234567890123456", in.getMsgId());

        String expect = "<xml><MsgType><![CDATA[text]]></MsgType>" +
            "<ToUserName><![CDATA[toUser]]></ToUserName>" +
            "<FromUserName><![CDATA[fromUser]]></FromUserName>" +
            "<CreateTime>1348831860</CreateTime>" +
            "<MsgId><![CDATA[1234567890123456]]></MsgId>" +
            "<Content><![CDATA[this is a test]]></Content></xml>";
        Assert.assertEquals(expect, mapper.writeValueAsString(in));
    }

}
