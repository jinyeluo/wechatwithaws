package lab.squirrel.service;

import lab.squirrel.pojo.CallbackMsgText;
import lab.squirrel.pojo.IncomingXmlMsg;
import org.junit.Test;

import static org.junit.Assert.*;

public class RRHistoryCacheTest {
    @Test
    public void testGetPut() throws Exception {
        RRHistoryCache rrHistoryCache = new RRHistoryCache(3);
        IncomingXmlMsg in1 = getIncomingXmlMsg("1");
        assertNull(rrHistoryCache.get(in1));

        rrHistoryCache.put(in1, getResponse("1"));
        assertEquals("1", rrHistoryCache.get(in1).getToUserName());

        IncomingXmlMsg in2 = getIncomingXmlMsg("2");
        rrHistoryCache.put(in2, getResponse("2"));
        assertEquals("1", rrHistoryCache.get(in1).getToUserName());
        assertEquals("2", rrHistoryCache.get(in2).getToUserName());

        IncomingXmlMsg in3 = getIncomingXmlMsg("3");
        rrHistoryCache.put(in3, getResponse("3"));
        assertEquals("1", rrHistoryCache.get(in1).getToUserName());
        assertEquals("2", rrHistoryCache.get(in2).getToUserName());
        assertEquals("3", rrHistoryCache.get(in3).getToUserName());

        IncomingXmlMsg in4 = getIncomingXmlMsg("4");
        rrHistoryCache.put(in4, getResponse("4"));
        assertEquals("2", rrHistoryCache.get(in2).getToUserName());
        assertEquals("3", rrHistoryCache.get(in3).getToUserName());
        assertEquals("4", rrHistoryCache.get(in4).getToUserName());
        assertNull(rrHistoryCache.get(in1));
    }

    private CallbackMsgText getResponse(String s) {
        CallbackMsgText callbackMsgText = new CallbackMsgText();
        callbackMsgText.setToUserName(s);
        return callbackMsgText;
    }

    private IncomingXmlMsg getIncomingXmlMsg(String s) {
        IncomingXmlMsg in = new IncomingXmlMsg();
        in.setMsgId(s);
        return in;
    }
}
