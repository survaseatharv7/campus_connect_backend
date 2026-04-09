package com.campusnexus.service.impl;

import com.google.firebase.cloud.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseStorageService.class);

    public String uploadFile(MultipartFile file, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = folder + "/" + UUID.randomUUID() + extension;

            var bucket = StorageClient.getInstance().bucket();
            var blob = bucket.create(fileName, file.getInputStream(), file.getContentType());
            blob.createAcl(com.google.cloud.storage.Acl.of(
                    com.google.cloud.storage.Acl.User.ofAllUsers(),
                    com.google.cloud.storage.Acl.Role.READER
            ));

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            return String.format("https://storage.googleapis.com/%s/%s",
                    bucket.getName(), encodedFileName);
        } catch (IOException e) {
            logger.error("Failed to upload file to Firebase: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
}
