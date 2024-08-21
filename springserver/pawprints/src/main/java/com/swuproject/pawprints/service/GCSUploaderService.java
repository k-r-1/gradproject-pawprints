package com.swuproject.pawprints.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class GCSUploaderService {

    @Value("${gcs.bucket.name}")
    private String bucketName;

    @Value("${gcs.credentials.path}")
    private String credentialsPath;

    private Storage storage;

    public GCSUploaderService() throws IOException {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(Files.newInputStream(Paths.get(credentialsPath))))
                .build()
                .getService();
    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        String fileName = folderName + "/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();
        storage.create(blobInfo, file.getBytes());

        return "gs://" + bucketName + "/" + fileName;
    }
}
