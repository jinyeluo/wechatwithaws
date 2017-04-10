package lab.squirrel.bearbay;

import com.amazonaws.services.s3.AmazonS3;
import lab.squirrel.function.WeChatFunctions;
import lab.squirrel.function.WeChatListener;
import lab.squirrel.pojo.CallbackMsg;
import lab.squirrel.pojo.CallbackMsgText;

import java.util.Properties;

public class BearBayWeChatListener extends WeChatFunctions implements WeChatListener {
    public BearBayWeChatListener(String bucketName, Properties properties, AmazonS3 s3Client) {
        super(bucketName, properties, s3Client);
    }

    @Override
    public CallbackMsg onText(String userId, String msg) {
        return getCallbackMsgText(userId + ":" + msg);
    }

    private CallbackMsgText getCallbackMsgText(String msg) {
        CallbackMsgText text = new CallbackMsgText();
        text.setContent(msg);
        return text;
    }

    @Override
    public CallbackMsg onFollowing(String userId, boolean subscribe) {
        if (subscribe) {
            return getCallbackMsgText(userId + ":" + "欢迎, 我们有最年轻的海鲜");
        } else {
            return getCallbackMsgText(userId + ":" + "Good luck");
        }
    }

    @Override
    public CallbackMsg onMenu(String userId, String menuId) {
        switch (menuId) {
            case "MN_MENU":
                return getCallbackMsgText(getProducts());

            case "MN_ORDER":
                break;

            case "MN_TOGO":
                break;

            case "MN_ORDER_CONFIRM":
                break;

            case "MN_CANCEL_ORDER":
                break;

            case "MN_FRESH":
                break;

            case "MN_SUPPORT":
                getCallbackMsgText("请联系松鼠实验室 jinyeluo@gmail.com\n我们用程序,而不是松鼠,做实验," );
            default:
                break;
        }
        return getCallbackMsgText("sorry, I don't understand");
    }

    private String getProducts() {
        return "缅因大龙虾 $15/lb\n" +
            "路易斯安那小龙虾 $10/lb\n" +
            "大虾 $12/lb\n" +
            " ameripure 大个生蚝 $12/lb\n" +
            "维吉尼亚活花蛤 \n" +
            "地中海活青口 \n" +
            "新西兰青口 \n" +
            "雪蟹 \n";
    }

    @Override
    public CallbackMsg onImage(String toUserName, String mediaId, String picUrl) {
        return getCallbackMsgText(toUserName + ":" + mediaId + ":" + picUrl);
    }

    @Override
    public CallbackMsg onAudio(String toUserName, String mediaId, String format) {
        return getCallbackMsgText(toUserName + ":" + mediaId + ":" + format);
    }

    @Override
    public CallbackMsg onVideo(String toUserName, String mediaId, String thumbMediaId) {
        return getCallbackMsgText(toUserName + ":" + mediaId + ":" + thumbMediaId);
    }

    @Override
    public CallbackMsg onLink(String toUserName, String title, String description, String url) {
        return getCallbackMsgText(toUserName + ":" + title + ":" + description + ":" + url);
    }
}
