package com.campusnexus.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folder, String resourceType);
    void deleteFile(String publicId);
}
