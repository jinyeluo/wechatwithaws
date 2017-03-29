package lab.squirrel.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CallbackMsgImageTest {
    @Test
    public void testReadWrite() throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        CallbackMsgImage in = mapper.readValue(
            mapper.getFactory().createParser(getClass().getResourceAsStream("/sample/callback_image.xml")),
            CallbackMsgImage.class);
        Assert.assertEquals("image", in.getMsgType());
        Assert.assertEquals("media_id", in.getImages().get(0).getMediaId());
    }
}
