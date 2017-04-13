package lab.squirrel.offline;

import lab.squirrel.bearbay.BearBayWeChatListener;
import lab.squirrel.function.CommonFunctions;
import lab.squirrel.function.S3Functions;
import lab.squirrel.function.WeChatFunctions;
import lab.squirrel.pojo.AccessTokenWeChatResponse;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LoadImage {

    public static void main(String[] args) throws IOException {
        String appProperties = args[0];
        String imageFileDir = args[1];
        String outputFile = args[2];
        Properties p = new Properties();
        p.load(new FileInputStream(appProperties));

        process(imageFileDir, p);
    }

    private static void process(String imageFileDir, Properties p){

        BearBayWeChatListener weChatFun = new BearBayWeChatListener();
        String bucket = weChatFun.getBucketName();
        weChatFun.setConfig(p);
        AccessTokenWeChatResponse accessToken = weChatFun.getTokenRemote(null);

        if (!accessToken.good())
            throw new RuntimeException("accessToken failed");

        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken.getAccess_token());
        params.put("type", "image");

        File[] files = new File(imageFileDir).listFiles();
        CommonFunctions commonFunctions = new CommonFunctions();
        for (File file : files) {
            try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                String menuResp = commonFunctions.httpPostBinaryCall("http", "file.api.wechat.com",
                    "/cgi-bin/media/upload", params, "testsample.jpg", in);
            } catch (Exception e) {
                //todo:
                throw new RuntimeException("accessToken failed");
            }
        }
    }

    private class MyS3Function extends S3Functions {
        public MyS3Function() {
            super(null);
        }

        @Override
        public Properties readS3ObjAsProperties(String s3bucket, String key) {
            return new Properties();
        }

        @Override
        public void writeToS3Obj(String bucket, String key, String content) {
        }
    }

}
