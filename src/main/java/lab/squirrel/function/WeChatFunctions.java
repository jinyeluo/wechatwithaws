package lab.squirrel.function;

import lab.squirrel.pojo.AccessTokenWeChatResponse;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.apache.log4j.Logger;

public class WeChatFunctions {
    static final Logger log = Logger.getLogger(WeChatFunctions.class);
    private final CommonFunctions commonFunctions = new CommonFunctions();
    private  S3Functions s3Functions;
    private Properties config;
    private static final String ACCESS_TOKEN_KEY = "workspace/access_token.properties";

    public WeChatFunctions() {
    }

    public void setConfig(Properties properties) {
        config = properties;
        s3Functions = createS3Functions();
    }


    protected S3Functions createS3Functions() {
        AmazonS3 s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());

        return new S3Functions(s3Client);
    }

    public String getAccessToken() {
        Properties accessToken = s3Functions.readS3ObjAsProperties(System.getenv(ConfigConst.S3BUCKET),
            ACCESS_TOKEN_KEY);

        boolean refresh = needRefreshAccessToken(accessToken);
        if (refresh) {
            String remoteCallContent = getTokenRemotely();
            if (remoteCallContent.trim().isEmpty()) {
                throw new RuntimeException("access token replied empty");
            }
            AccessTokenWeChatResponse accessTokenResp = commonFunctions.jsonToObj(remoteCallContent, AccessTokenWeChatResponse.class);
            if (!accessTokenResp.good()) {
                throw new RuntimeException("access token call failed:" + commonFunctions.objectToJson(accessTokenResp));
            }
            int expires_in = accessTokenResp.getExpires_in();
            accessToken.setProperty("access_token", accessTokenResp.getAccess_token());
            accessToken.setProperty("expires_at", Long.toString(System.currentTimeMillis() + expires_in * 1000));
            String accessTokenInStr = getPropertyAsString(accessToken);
            s3Functions.writeToS3Obj(System.getenv(ConfigConst.S3BUCKET),
                ACCESS_TOKEN_KEY, accessTokenInStr);

            log.info("access token saved");
        }

        return accessToken.getProperty("access_token");
    }

    private String getPropertyAsString(Properties prop) {
        StringWriter writer = new StringWriter();
        prop.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }

    private boolean needRefreshAccessToken(Properties accessToken) {
        boolean needRefresh = false;
        String expires_at = accessToken.getProperty("expires_at");
        if (expires_at == null) {
            needRefresh = true;
        } else {
            long expires = Long.parseLong(expires_at);
            if (expires - 500 < System.currentTimeMillis()) {
                needRefresh = true;
            }
        }
        return needRefresh;
    }

    private String getTokenRemotely() {
        log.info("get token remotely");
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credential");
        params.put("appid", config.getProperty(ConfigConst.APP_ID));
        params.put("secret", config.getProperty(ConfigConst.APPSECRET));
        //https://api.wechat.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
        return commonFunctions.httpGetCall("https", "api.wechat.com", "/cgi-bin/token", params);
    }

    /**
     * private function checkSignature()
     * {
     * $signature = $_GET["signature"];
     * $timestamp = $_GET["timestamp"];
     * $nonce = $_GET["nonce"];
     * <p>
     * $token = TOKEN;
     * $tmpArr = array($token, $timestamp, $nonce);
     * sort($tmpArr, SORT_STRING);
     * $tmpStr = implode( $tmpArr );
     * $tmpStr = sha1( $tmpStr );
     * <p>
     * if( $tmpStr == $signature ){
     * return true;
     * }else{
     * return false;
     * }
     * }
     */
    public String messageAuthentication(Map<String, Object> querystring) {
        String signature = String.valueOf(querystring.get("signature"));
        String nonce = String.valueOf(querystring.get("nonce"));
        String timestamp = String.valueOf(querystring.get("timestamp"));
        String echostr = String.valueOf(querystring.get("echostr"));

        String token = config.getProperty(ConfigConst.TOKEN);
        String[] arr = {token, timestamp, nonce};
        Arrays.sort(arr);
        String implode = new StringBuilder().append(arr[0]).append(arr[1]).append(arr[2]).toString();
        String sha1Str = sha1(implode);
        if  (sha1Str.equals(signature)) {
            return echostr;
        } else {
            return "verification failed";
        }
    }

    /**
     * $str = 'apple';
     * <p>
     * if (sha1($str) === 'd0be2dc421be4fcd0172e5afceea3970e2f3d940') {
     * echo "Would you like a green or red apple?";
     * }
     */
    String sha1(String password) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            return byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
