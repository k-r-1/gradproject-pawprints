package com.swuproject.pawprints.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class GCSUploaderService {

    private final String bucketName;
    private final Storage storage;

    // 생성자를 통해 GCS에 연결
    public GCSUploaderService(
            @Value("${gcs.bucket.name}") String bucketName,
            @Value("${gcs.credentials.path}") String credentialsPath) throws IOException {
        this.bucketName = bucketName;

        // ClassPathResource를 사용하여 클래스패스에서 자격 증명 파일을 읽기
        Resource resource = new ClassPathResource(credentialsPath);
        try (InputStream credentialsStream = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");
            this.storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        }
    }

    // 파일 업로드 메소드
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        // 고유한 파일명을 생성
        String fileName = folderName + "/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Blob 정보를 생성하여 GCS에 업로드
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();
        storage.create(blobInfo, file.getBytes());

        // 업로드된 파일의 GCS URL을 반환
        return "gs://" + bucketName + "/" + fileName;
    }

    // 파일 삭제 메소드
    public void deleteFile(String filePath) throws IOException {
        // GCS에서 파일을 삭제하는 코드
        Blob blob = storage.get(BlobId.of(bucketName, filePath));
        if (blob != null && blob.exists()) {
            blob.delete();
        }
    }
}
