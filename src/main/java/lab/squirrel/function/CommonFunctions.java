package lab.squirrel.function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommonFunctions {
    private static final String UTF_8 = java.nio.charset.StandardCharsets.UTF_8.name();
    private static final Charset UTF_8_CHARSET = java.nio.charset.StandardCharsets.UTF_8;

    public CommonFunctions() {
    }

    String httpGetCall(String scheme, String host,
                       String path, Map<String, String> params) {
        try {
            URLConnection connection = getUrlConnection(scheme, host, path, params);
            connection.setRequestProperty("Accept-Charset", UTF_8);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);

            StringBuilder content = new StringBuilder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (content.length() > 0) {
                        content.append("&");
                    }
                    content.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }

            connection.connect();

            try (InputStream response = connection.getInputStream()) {
                return inputStreamToString(response);

            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String inputStreamToString(InputStream response) {
        Scanner s = new Scanner(response, UTF_8).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    String httpPostCall(String scheme, String host,
                        String path,
                        Map<String, String> params, String content) {
        try {
            URLConnection connection = getUrlConnection(scheme, host, path, params);

            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", UTF_8);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(content.getBytes(UTF_8));
            }

            try (InputStream response = connection.getInputStream()) {
                return inputStreamToString(response);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URLConnection getUrlConnection(String scheme, String host, String path, Map<String, String> params) throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setCharset(UTF_8_CHARSET);
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(path);

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
        }
        URL url = builder.build().toURL();
        System.out.println(url.toString());

        return url.openConnection();
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> jsonToMap(String json) {
        try {
            return new ObjectMapper().readValue(json, HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    <T> T jsonToObj(String json, Class<T> tClass) {
        try {
            return new ObjectMapper().readValue(json, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String objectToJson(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
