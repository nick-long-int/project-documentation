package ru.gnidenko.repo;

import ru.gnidenko.config.S3Config;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.util.Random;

public class S3Repo {
    private final S3Client s3Client;
    private final String bucketName;

    public S3Repo(String bucketName) {
        s3Client = S3Config.getClient();
        this.bucketName = bucketName;
    }

    public String upload(String pathToFile){
        File file = new File(pathToFile);
        String fileKey = generateKey(file.getName());


        s3Client.putObject(request ->
            request
                .bucket(bucketName)
                .key(fileKey)
            , file.toPath()
        );

        return fileKey;
    }

    public ResponseInputStream<GetObjectResponse> download(String key, String downloadPath){
        return s3Client.getObject(request ->
            request
                .bucket(bucketName)
                .key(key));
    }

    private String generateKey(String fileName){
        String[] str = fileName.split("\\.");
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        builder.append(random.nextInt(10))
            .append(str[0])
            .append(random.nextInt(11000))
            .append(".")
            .append(str[1]);
        return builder.toString();
    }
}
