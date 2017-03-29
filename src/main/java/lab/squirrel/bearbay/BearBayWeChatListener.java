package lab.squirrel.bearbay;

import lab.squirrel.function.WeChatFunctions;
import lab.squirrel.function.WeChatListener;
import lab.squirrel.pojo.CallbackMsg;
import lab.squirrel.pojo.CallbackMsgText;

public class BearBayWeChatListener extends WeChatFunctions implements WeChatListener {
    public BearBayWeChatListener() {
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
            return getCallbackMsgText(userId + ":" + "欢迎，我们有最年轻的海鲜");
        } else {
            return getCallbackMsgText(userId + ":" + "Good luck");
        }
    }

    @Override
    public CallbackMsg onMenu(String userId, String menuId) {
        return getCallbackMsgText(userId + ":" + menuId);
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
