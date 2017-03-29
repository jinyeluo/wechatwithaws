package lab.squirrel.service;

import lab.squirrel.bearbay.BearBayWeChatListener;
import lab.squirrel.function.ConfigConst;
import lab.squirrel.function.S3Functions;
import lab.squirrel.function.WeChatListener;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lab.squirrel.pojo.*;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class SquirrelEndPoint {

    @SuppressWarnings("unchecked")
    public String handleGetRequest(Map<String, Object> input, Context context) {
        WeChatListener myWeChat = getWeChatListener();
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            context.getLogger().log(entry.getKey() + "->" + entry.getValue());
        }

        Map<String, Object> params = (Map<String, Object>) input.get("params");
        if (params != null) {
            Map<String, Object> querystring = (Map<String, Object>) params.get("querystring");
            String verification = myWeChat.messageAuthentication(querystring);
            context.getLogger().log(verification);
            return verification;
        }

        return "unexpected call";
    }

    private WeChatListener getWeChatListener() {
        AmazonS3 s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
        Properties properties = new S3Functions(s3Client).readS3ObjAsProperties(
            System.getenv(ConfigConst.S3BUCKET), "data/app.properties");
        WeChatListener myWeChat = new BearBayWeChatListener();
        myWeChat.setConfig(properties);
        return myWeChat;
    }

    @SuppressWarnings("unchecked")
    public String handlePostRequest(Map<String, Object> input, Context context) {
        WeChatListener myWeChat = getWeChatListener();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            context.getLogger().log(entry.getKey() + "->" + entry.getValue());
        }

        String requestData = String.valueOf(input.get("body-json"));
        if (requestData != null) {
            try {
                String resp = handleRequest(requestData, myWeChat);
                if (resp == null)
                    return "service error occurred";
                else return resp;
            } catch (IOException e) {
                context.getLogger().log(e.getMessage());
            }
        }

        return "service error occurred";
    }

    private String handleRequest(String data, WeChatListener myWeChat) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        XmlMsg in = mapper.readValue(data, XmlMsg.class);
        if (in == null || in.getMsgType() == null) {
            return null;
        }

        CallbackMsg response = null;
        switch (in.getMsgType().toLowerCase()) {
            case "text":
                InComingMsgText inComingMsgText = mapper.readValue(data, InComingMsgText.class);
                response = myWeChat.onText(inComingMsgText.getToUserName(), inComingMsgText.getContent());
                break;

            case "image":
                InComingMsgImage inComingMsgImage = mapper.readValue(data, InComingMsgImage.class);
                response = myWeChat.onImage(inComingMsgImage.getToUserName(),
                    inComingMsgImage.getMediaId(), inComingMsgImage.getPicUrl());
                break;

            case "voice":
                InComingMsgAudio inComingMsgAudio = mapper.readValue(data, InComingMsgAudio.class);

                response = myWeChat.onAudio(inComingMsgAudio.getToUserName(),
                    inComingMsgAudio.getMediaId(), inComingMsgAudio.getFormat());
                break;

            case "video":
                InComingMsgVideo inComingMsgVideo = mapper.readValue(data, InComingMsgVideo.class);

                response = myWeChat.onVideo(inComingMsgVideo.getToUserName(),
                    inComingMsgVideo.getMediaId(), inComingMsgVideo.getThumbMediaId());
                break;

            case "link":
                InComingMsgLink inComingMsgLink = mapper.readValue(data, InComingMsgLink.class);

                response = myWeChat.onLink(inComingMsgLink.getToUserName(),
                    inComingMsgLink.getTitle(), inComingMsgLink.getDescription(), inComingMsgLink.getUrl());
                break;

            case "event":
                InComingMsgEvent inComingMsgEvent = mapper.readValue(data, InComingMsgEvent.class);

                switch (inComingMsgEvent.getEvent().toLowerCase()) {
                    case "subscribe":
                        response = myWeChat.onFollowing(inComingMsgEvent.getToUserName(), true);
                        break;
                    case "unsubscribe":
                        response = myWeChat.onFollowing(inComingMsgEvent.getToUserName(), false);
                        break;
                    case "click":
                        response = myWeChat.onMenu(inComingMsgEvent.getToUserName(), inComingMsgEvent.getEventKey());
                        break;
                }
                break;


            default:
        }

        if (response != null) {
            response.setToUserName(in.getFromUserName());
            response.setFromUserName(in.getToUserName());
            response.setCreateTime(System.currentTimeMillis());
        }

        return mapper.writeValueAsString(response);
    }
}
