package lab.squirrel.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "xml")
public class InComingMsgImage extends IncomingXmlMsg {
    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;
    @JacksonXmlProperty(localName = "PicUrl")
    private String picUrl;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
