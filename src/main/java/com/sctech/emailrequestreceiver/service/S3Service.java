package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.dto.FileUploadResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class S3Service {
    private static final Logger logger = LogManager.getLogger(S3Service.class);
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public S3Service(@Value("${aws.secret.key}") String secretKey, @Value("${aws.access.key}") String accessKey) {
        this.s3Client = S3Client.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

    public void uploadFile(String keyName, Path filePath) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build(),
                RequestBody.fromFile(filePath));
    }

    public void uploadFile(String keyName, MultipartFile file) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public void uploadFile(String keyName, byte[] fileContent) {
        logger.debug("Uploading file to S3 : " + keyName);
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build(),
                RequestBody.fromBytes(fileContent));
    }

    public void uploadFileFromZip(MultipartFile zipFile, String targetFileName, String keyName) {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals(targetFileName)) {
                    byte[] fileData = zis.readAllBytes();
                    s3Client.putObject(PutObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(keyName)
                                    .build(),
                            RequestBody.fromBytes(fileData));
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Error reading zip file : " + e.getMessage());
            throw new RuntimeException("Error reading zip file");
        }
    }

    public void downloadFile(String keyName, Path downloadPath) {
        s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build(),
                downloadPath);
    }

    public byte[] downloadFileContent(String keyName) throws IOException {
        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build())) {
            return s3Object.readAllBytes();
        }
    }
}
