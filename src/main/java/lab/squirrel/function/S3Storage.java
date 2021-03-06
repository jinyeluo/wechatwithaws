package lab.squirrel.function;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class S3Storage implements DataStorage {
    private static final Charset UTF_8_CHARSET = java.nio.charset.StandardCharsets.UTF_8;
    private final AmazonS3 s3Client;
    private final CommonFunctions helper = new CommonFunctions();

    public S3Storage(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String readAsStr(String bucket, String key) {
        try {
            try (S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key))) {
                try (InputStream objectData = object.getObjectContent()) {
                    return helper.inputStreamToString(objectData);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties readAsProperties(String bucket, String key) {
        try (S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key))) {
            return readAsProperties(object);
        } catch (IOException|AmazonS3Exception e) {
            throw new RuntimeException("error reading " + bucket + ":" + key, e);
        }
    }

    private Properties readAsProperties(S3Object input) {
        try (InputStream objectData = input.getObjectContent()) {
            Properties result = new Properties();
            result.load(new InputStreamReader(objectData));
            return result;
        } catch (IOException|AmazonS3Exception e) {
            throw new RuntimeException("error reading s3", e);
        }
    }

    public void write(String bucket, String key, String content) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");
        metadata.setContentLength(content.length());
        InputStream stream = new ByteArrayInputStream(content.getBytes(UTF_8_CHARSET));

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, stream, metadata);
        s3Client.putObject(putObjectRequest);
    }
}
