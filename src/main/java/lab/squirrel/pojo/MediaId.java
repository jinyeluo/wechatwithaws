package lab.squirrel.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MediaId {
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;

    public MediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
