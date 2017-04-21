package lab.squirrel.sample;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lab.squirrel.function.*;
import lab.squirrel.pojo.CallbackMsg;
import lab.squirrel.pojo.CallbackMsgText;
import lab.squirrel.service.SquirrelEndPoint;

import javax.servlet.ServletContext;
import java.util.Map;

import static lab.squirrel.function.ConfigConst.DATA_STORAGE;
import static lab.squirrel.function.ConfigConst.S3;

public class SampleWeChatListener implements WeChatListener  {
    private WeChatFunctions weChatFunctions;

    @Override
    public void setup(SquirrelEndPoint squirrelEndPoint) {
        squirrelEndPoint.setToLog(true);

        String bucket = squirrelEndPoint.getInitParameter(ConfigConst.S3BUCKET);
        DataStorage dataStorage;
        if (S3.equals(squirrelEndPoint.getInitParameter(DATA_STORAGE))) {
            AmazonS3 s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
            dataStorage = new S3Storage(s3Client);
        } else {
            dataStorage = new LocalStorage();
        }
        weChatFunctions = new WeChatFunctions(bucket, dataStorage);
    }

    @Override
    public CallbackMsg onText(String userId, String msg) {
        return getCallbackMsg("text:" + userId + ":" + msg);
    }

    @Override
    public CallbackMsg onFollowing(String userId, boolean subscribe) {
        return getCallbackMsg("welcome:" + userId);
    }

    @Override
    public CallbackMsg onMenu(String userId, String menuId) {
        return getCallbackMsg("menu:" + userId + ":" + menuId);
    }

    @Override
    public String messageAuthentication(Map<String, String[]> querystring, ServletContext servletContext) {
        return weChatFunctions.messageAuthentication(querystring, servletContext);
    }

    @Override
    public CallbackMsg onImage(String toUserName, String mediaId, String picUrl) {
        return getCallbackMsg("image:" + toUserName + ":" + mediaId + ":" + picUrl);
    }

    @Override
    public CallbackMsg onAudio(String toUserName, String mediaId, String format) {
        return getCallbackMsg("audio:" + toUserName + ":" + mediaId + ":" + format);
    }

    @Override
    public CallbackMsg onVideo(String toUserName, String mediaId, String thumbMediaId) {
        return getCallbackMsg("video:" + toUserName + ":" + mediaId + ":" + thumbMediaId);
    }

    @Override
    public CallbackMsg onLink(String toUserName, String title, String description, String url) {
        return getCallbackMsg("link:" + toUserName + ":" + title + ":" + description + ":" + url);
    }

    private CallbackMsg getCallbackMsg(String content) {
        CallbackMsgText response = new CallbackMsgText();
        response.setContent(content);
        return response;
    }
}
