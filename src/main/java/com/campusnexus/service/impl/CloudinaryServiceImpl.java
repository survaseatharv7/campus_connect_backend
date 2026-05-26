package com.campusnexus.service.impl;

import com.campusnexus.service.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder, String resourceType) {
        try {
            java.util.Map<String, Object> options = new java.util.HashMap<>();
            options.put("folder", folder);
            options.put("resource_type", resourceType);
            if ("raw".equals(resourceType)) {
                options.put("use_filename", true);
                options.put("unique_filename", true);
            }
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Cloudinary delete failed", e);
        }
    }
}
