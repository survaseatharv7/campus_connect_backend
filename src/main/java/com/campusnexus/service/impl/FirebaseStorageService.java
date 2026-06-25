package com.campusnexus.service.impl;

import com.google.firebase.cloud.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
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

    /**
     * Delete a file from Firebase Storage by its public URL.
     *
     * <p>The URL format is expected to be:
     * {@code https://storage.googleapis.com/{bucket}/{encoded-path}}
     *
     * <p>This method is best-effort: callers should catch any exception and log it
     * rather than letting image cleanup failures block the primary operation.
     *
     * @param fileUrl the public Firebase Storage URL returned by {@link #uploadFile}
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        try {
            // Extract the blob path from the URL
            // URL pattern: https://storage.googleapis.com/{bucket}/{encoded-path}
            var bucket = StorageClient.getInstance().bucket();
            String bucketPrefix = "https://storage.googleapis.com/" + bucket.getName() + "/";
            if (!fileUrl.startsWith(bucketPrefix)) {
                logger.warn("Cannot delete file — URL does not match expected Firebase pattern: {}", fileUrl);
                return;
            }
            String encodedPath = fileUrl.substring(bucketPrefix.length());
            String blobPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);

            var blob = bucket.get(blobPath);
            if (blob != null && blob.exists()) {
                blob.delete();
                logger.info("Deleted Firebase Storage file: {}", blobPath);
            } else {
                logger.warn("Firebase Storage file not found (already deleted?): {}", blobPath);
            }
        } catch (Exception e) {
            logger.error("Failed to delete Firebase Storage file at {}: {}", fileUrl, e.getMessage());
            throw new RuntimeException("Failed to delete file from Firebase: " + e.getMessage(), e);
        }
    }
}

