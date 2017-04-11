package lab.squirrel.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

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

    @Test
    public void test() {
    }
}
