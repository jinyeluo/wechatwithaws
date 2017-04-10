package lab.squirrel.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "xml")
public class CallbackMsgImage extends CallbackMsg {
    @JacksonXmlProperty(localName = "Image")
    private List<MediaId> images;

    public CallbackMsgImage() {
        setMsgType("image");
    }

    public List<MediaId> getImages() {
        return images;
    }

    public void setImages(List<MediaId> images) {
        this.images = images;
    }
}
