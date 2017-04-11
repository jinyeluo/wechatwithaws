package lab.squirrel.bearbay;

import com.amazonaws.services.s3.AmazonS3;
import lab.squirrel.function.WeChatFunctions;
import lab.squirrel.function.WeChatListener;
import lab.squirrel.pojo.CallbackMsg;
import lab.squirrel.pojo.CallbackMsgText;

import java.util.Properties;

public class BearBayWeChatListener extends WeChatFunctions implements WeChatListener {
    private OrderHandler orderHandler = new OrderHandler();

    public BearBayWeChatListener(String bucketName, Properties properties, AmazonS3 s3Client) {
        super(bucketName, properties, s3Client);
    }

    @Override
    public CallbackMsg onText(String userId, String msg) {
        String orderMsg = orderHandler.order(userId, msg, products());
        if (orderMsg == null) {
            orderMsg = "Sorry, I don't understand your msg";
        }
        return getCallbackMsgText(orderMsg);
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
                return onNewOrder(userId, Order.DINE_IN);

            case "MN_TOGO":
                return onNewOrder(userId, Order.TO_GO);

            case "MN_CONFRM": {
                StringBuilder answer = new StringBuilder();
                boolean success = orderHandler.confirm(userId, products(), answer);
                return getCallbackMsgText(answer.toString());
            }

            case "MN_CANCEL":
                return getCallbackMsgText(orderHandler.cancel(userId));

            case "MN_FRESH":
                return getCallbackMsgText(getProducts());

            case "MN_SUPPORT":
                return getCallbackMsgText("请联系松鼠实验室 jinyeluo@gmail.com\n我们用程序,而不是松鼠,做实验");
            default:
                break;
        }
        return getCallbackMsgText("sorry, I don't understand");
    }

    private CallbackMsg onNewOrder(String userId, String dineType) {
        try {
            StringBuilder answer = new StringBuilder();
            boolean success = orderHandler.newOrder(userId, dineType, answer);
            if (success)
                return getCallbackMsgText(getMenu());
            else
                return getCallbackMsgText(answer.toString());
        } catch (OrderHandlerException e) {
            return getCallbackMsgText(e.getMessage());
        }
    }

    private String getMenu() {
        return getWhatIsHot() +
            "A 缅因大龙虾\n" +
            "B 路易斯安那小龙虾\n" +
            "C 大虾\n" +
            "D ameripure 大个生蚝\n" +
            "E 维吉尼亚活花蛤\n" +
            "F 地中海活青口\n" +
            "G 新西兰青口\n" +
            "格式：菜号（A-G）#份量（1-10）#辣度（1-10)\n" +
            "例如 路易斯安那小龙虾五磅，辣度三，\n" +
            "请回复  B#5#3\n" +
            "一个回复选一个菜式。可多次回复。\n" +
            "如预订，请回复 *等待* 时间。\n形式: “小时h分钟”。 \n比如等半小时“0h30”，或三小时“3h”\n" +
            "最后选用菜单“确定”完成。";
    }

    private String getWhatIsHot() {
        return "龙虾热卖中。。。\n";
    }

    private String getProducts() {
        return "缅因大龙虾 $15/lb\n" +
            "路易斯安那小龙虾 $10/lb\n" +
            "大虾 $12/lb\n" +
            "ameripure 大个生蚝 $12/lb\n" +
            "维吉尼亚活花蛤 \n" +
            "地中海活青口 \n" +
            "新西兰青口 \n" +
            "雪蟹 \n";
    }

    private Products products() {
        return new Products();
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
