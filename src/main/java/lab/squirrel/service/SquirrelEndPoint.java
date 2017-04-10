package lab.squirrel.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lab.squirrel.bearbay.BearBayWeChatListener;
import lab.squirrel.function.CommonFunctions;
import lab.squirrel.function.S3Functions;
import lab.squirrel.function.WeChatListener;
import lab.squirrel.pojo.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

public class SquirrelEndPoint extends HttpServlet {
    private final WeChatListener myWeChat;
    private boolean toLog = true;

    public SquirrelEndPoint() {
        AmazonS3 s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        Properties properties = new S3Functions(s3Client).readS3ObjAsProperties(
            getBucketName(), "data/app.properties");
        myWeChat = new BearBayWeChatListener(getBucketName(), properties, s3Client);
    }

    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            for (String value : entry.getValue()) {
                getServletContext().log(entry.getKey() + "=" + value);
            }
        }

        String verification = myWeChat.messageAuthentication(paramMap, getServletContext());
        getServletContext().log(verification);

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print(verification);
    }

    protected String getBucketName() {
        return "bearbay.svc";
    }

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse res)
        throws IOException, ServletException {
        res.setContentType("application/xml; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        String input = new CommonFunctions().inputStreamToString(request.getInputStream());
        info(input);
        if (input != null) {
            try {
                String response = handleRequest(input, myWeChat);
                if (response == null)
                    out.print("service error occurred");
                else {
                    info(response);
                    out.print(response);
                }
            } catch (IOException e) {
                getServletContext().log(e.getMessage());
            }
        } else
            out.print("service error occurred");
        out.flush();
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

    private void info(String msg) {
        if (toLog) {
            log(msg);
        }
    }
}
