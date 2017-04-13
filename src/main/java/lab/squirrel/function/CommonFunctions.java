package lab.squirrel.function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
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

    public String inputStreamToString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream, UTF_8).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String httpPostTxCall(String scheme, String host,
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

    public String httpPostBinaryCall(String scheme, String host,
                                     String path,
                                     Map<String, String> params,
                                     String binaryFile, InputStream inStrm) {
        try {
            String charset = "UTF-8";
            String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
            String CRLF = "\r\n"; // Line separator required by multipart/form-data.

            URLConnection connection = getUrlConnection(scheme, host, path, params);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            ) {
                // Send binary file.
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\""
                    + binaryFile + "\"").append(CRLF);
                writer.append("Content-Type: "
                    + URLConnection.guessContentTypeFromName(binaryFile)).append(CRLF);
                writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                writer.append(CRLF).flush();
                IOUtils.copy(inStrm, output);
                output.flush(); // Important before continuing with writer!
                writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

                // End of multipart/form-data.
                writer.append("--" + boundary + "--").append(CRLF).flush();
            }

            // Request is lazily fired whenever you need to obtain information about response.
            int responseCode = ((HttpURLConnection) connection).getResponseCode();
            System.out.println(responseCode); // Should be 200
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
    public Map<String, Object> jsonToMap(String json) {
        try {
            return new ObjectMapper().readValue(json, HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T jsonToObj(String json, Class<T> tClass) {
        try {
            return new ObjectMapper().readValue(json, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String objectToJson(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
