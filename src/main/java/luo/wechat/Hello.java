package luo.wechat;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Hello {

    private static final String BUCKET_NAME = "luo.wechat";

    public String handleRequest(Map<String, Object> input, Context context) {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            context.getLogger().log(entry.getKey() + "->" + entry.getValue());
        }

        Map<String, Object> params = (Map<String, Object>) input.get("params");
        if (params != null) {
            Map<String, Object> querystring = (Map<String, Object>) params.get("querystring");
            String signature = String.valueOf(querystring.get("signature"));
            String nonce = String.valueOf(querystring.get("nonce"));
            String timestamp = String.valueOf(querystring.get("timestamp"));
            String echostr = String.valueOf(querystring.get("echostr"));

            for (Map.Entry<String, Object> entry : querystring.entrySet()) {
                b.append(entry.getKey()).append("->").append(entry.getValue()).append("\n");
            }
        }

        try {
            opens3(context);
        } catch (IOException e) {
            context.getLogger().log(e.getMessage());
        }

        return b.toString();
    }

    private void opens3(Context context) throws IOException {
//        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        AmazonS3 s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());

        context.getLogger().log("opening s3" + "workspace/dellLargeCluster.txt");
        try (S3Object object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME,
            "workspace/dellLargeCluster.txt"))) {
            context.getLogger().log("opening file" + "workspace/dellLargeCluster.txt");
            try (InputStream objectData = object.getObjectContent()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
                context.getLogger().log("reading s3" + "workspace/dellLargeCluster.txt");
                String line = reader.readLine();
                if (line != null) {
                    context.getLogger().log("file content" + line);
                    line = reader.readLine();
                    context.getLogger().log("reading line" + "workspace/dellLargeCluster.txt");

                }
            }
        }

        String key = "workspace/writer.txt";
        String content = "new file content";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");
        metadata.setContentLength(content.length());
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        PutObjectRequest putObjectRequest = new PutObjectRequest(
            BUCKET_NAME, key, stream, metadata);
        s3Client.putObject(putObjectRequest);
    }
}
