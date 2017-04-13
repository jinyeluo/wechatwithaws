package lab.squirrel.pojo;

import lab.squirrel.function.CommonFunctions;
import org.junit.Assert;
import org.junit.Test;

public class ImageUploadingWeChatResponseTest {
    @Test
    public void testJson() {
        String in = "{\"type\":\"image\","
            + "\"media_id\":\"mSCEgpG3t8oSFwI1hCe4cJSUZE6pwU6-PYOJXj4VMwPvRHMSosTFpKgJO13kd3xO\","
            + "\"created_at\":1491955310}";

        CommonFunctions commonFunctions = new CommonFunctions();
        ImageUploadingWeChatResponse resp = commonFunctions.jsonToObj(in, ImageUploadingWeChatResponse.class);
        Assert.assertEquals(1491955310, resp.getCreated_at());
        Assert.assertEquals("image", resp.getType());
        Assert.assertEquals("mSCEgpG3t8oSFwI1hCe4cJSUZE6pwU6-PYOJXj4VMwPvRHMSosTFpKgJO13kd3xO",
            resp.getMedia_id());

    }
}
