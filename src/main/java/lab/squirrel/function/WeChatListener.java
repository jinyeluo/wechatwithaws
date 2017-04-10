package lab.squirrel.function;

import lab.squirrel.pojo.CallbackMsg;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.Properties;

public interface WeChatListener {

    /**
     * This function is called when a user sends a msg
     *
     * @param userId id of the user
     * @param msg    content of the user
     * @return response to user
     */
    CallbackMsg onText(String userId, String msg);

    /**
     * This function is called when a new person follows the account, or quit following
     *
     * @param userId    from user id
     * @param subscribe true for following, false unfollowing
     * @return response to user
     */
    CallbackMsg onFollowing(String userId, boolean subscribe);

    /**
     * This function is called when a user clicks on a menu
     *
     * @param userId from user id
     * @param menuId id of the menu
     * @return response to user
     */
    CallbackMsg onMenu(String userId, String menuId);

    /**
     * Before any traffic sent from WeChat, WeChat will require an API verification described at
     * http://admin.wechat.com/wiki/index.php?title=Getting_Started.
     * This method is to handle that specific request.
     * @param querystring contains signature/timestamp/nonce/echostr
     * @param servletContext
     * @return echostr
     */
    String messageAuthentication(Map<String, String[]> querystring, ServletContext servletContext);

    CallbackMsg onImage(String toUserName, String mediaId, String picUrl);

    CallbackMsg onAudio(String toUserName, String mediaId, String format);

    CallbackMsg onVideo(String toUserName, String mediaId, String thumbMediaId);

    CallbackMsg onLink(String toUserName, String title, String description, String url);
}
