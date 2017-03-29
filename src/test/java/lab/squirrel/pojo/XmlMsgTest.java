package lab.squirrel.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class XmlMsgTest {
    @Test
    public void testXmlReader() throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        XmlMsg in = mapper.readValue(
            mapper.getFactory().createParser(getClass().getResourceAsStream("/sample/in_comming_msg.xml")),
            XmlMsg.class);
        assertEquals("text", in.getMsgType());
    }

}
