package lab.squirrel.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "xml")
public class InComingMsgEvent extends IncomingXmlMsg {
    @JacksonXmlProperty(localName = "Event")
    private String event;
    @JacksonXmlProperty(localName = "EventKey")
    private String eventKey;
    @JacksonXmlProperty(localName = "Ticket")
    private String ticket;


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
