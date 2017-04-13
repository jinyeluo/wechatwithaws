package lab.squirrel.function;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lab.squirrel.pojo.AccessTokenWeChatResponse;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WeChatFunctions {
    private static final String ACCESS_TOKEN_KEY = "workspace/access_token.properties";

    private CommonFunctions commonFunctions = new CommonFunctions();
    private String bucketName;
    private S3Functions s3Functions;
    private Properties config;

    public WeChatFunctions() {
    }

    protected void config(String bucketName) {
        this.bucketName = bucketName;
        AmazonS3 s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        config = new S3Functions(s3Client).readS3ObjAsProperties(
            bucketName, "data/app.properties");
        s3Functions = new S3Functions(s3Client);
    }

    public void setConfig(Properties p) {
        config = p;
    }

    protected void setS3FunctionForTesting(S3Functions s3fun) {
        s3Functions = s3fun;
    }

    public String getAccessToken(ServletContext servletContext) {
        Properties accessToken = s3Functions.readS3ObjAsProperties(bucketName,
            ACCESS_TOKEN_KEY);

        boolean refresh = needRefreshAccessToken(accessToken);
        if (refresh) {
            AccessTokenWeChatResponse accessTokenResp = getTokenRemote(servletContext);
            int expires_in = accessTokenResp.getExpires_in();
            accessToken.setProperty("access_token", accessTokenResp.getAccess_token());
            accessToken.setProperty("expires_at", Long.toString(System.currentTimeMillis() + expires_in * 1000));
            String accessTokenInStr = getPropertyAsString(accessToken);
            s3Functions.writeToS3Obj(System.getenv(ConfigConst.S3BUCKET),
                ACCESS_TOKEN_KEY, accessTokenInStr);

            log(servletContext, "access token saved");
        }

        return accessToken.getProperty("access_token");
    }

    public AccessTokenWeChatResponse getTokenRemote(ServletContext servletContext) {
        String remoteCallContent = remoteGetToken(servletContext);
        if (remoteCallContent.trim().isEmpty()) {
            throw new RuntimeException("access token replied empty");
        }
        AccessTokenWeChatResponse accessTokenResp = commonFunctions.jsonToObj(remoteCallContent,
            AccessTokenWeChatResponse.class);
        if (!accessTokenResp.good()) {
            throw new RuntimeException("access token call failed:" + commonFunctions.objectToJson(accessTokenResp));
        }
        return accessTokenResp;
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

    private String remoteGetToken(ServletContext servletContext) {
        log(servletContext, "get token remotely");
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
    public String messageAuthentication(Map<String, String[]> querystring, ServletContext servletContext) {
        String signature = getValue(querystring.get("signature"));
        String nonce = getValue(querystring.get("nonce"));
        String timestamp = getValue(querystring.get("timestamp"));
        String echostr = getValue(querystring.get("echostr"));

        log(servletContext, new StringBuilder().append("auth:").append(signature).append(":")
            .append(nonce).append(":").append(timestamp).append(":").append(echostr).toString());
        String token = config.getProperty(ConfigConst.TOKEN).trim();
        String[] arr = {token, timestamp, nonce};
        Arrays.sort(arr);
        String implode = new StringBuilder().append(arr[0]).append(arr[1]).append(arr[2]).toString();
        String sha1Str = sha1(implode);
        if (sha1Str.equals(signature)) {
            return echostr;
        } else {
            return "verification failed:[" + sha1Str + "]:[" + signature + "]:[" + token + "]";
        }
    }

    private void log(ServletContext servletContext, String s) {
        if (servletContext != null) {
            servletContext.log(s);
        }
    }

    private String getValue(String[] values) {
        if (values != null && values.length > 0)
            return values[0];
        else return "";
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
