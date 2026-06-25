package com.campusnexus.controller;

import com.campusnexus.dto.ApiResponse;
import com.campusnexus.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Upload", description = "Generic file upload endpoints")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/image")
    @Operation(summary = "Upload general image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file, "campusnexus/images", "image");
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", Map.of("url", url)));
    }

    @PostMapping("/profile-pic")
    @Operation(summary = "Upload profile picture")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfilePic(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file, "campusnexus/profiles", "image");
        return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded successfully", Map.of("url", url)));
    }

    @PostMapping("/document")
    @Operation(summary = "Upload document")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadDocument(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file, "campusnexus/documents", "raw");
        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", Map.of("url", url)));
    }

    @PostMapping("/event-image")
    @Operation(summary = "Upload event poster/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadEventImage(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file, "campusnexus/events", "image");
        return ResponseEntity.ok(ApiResponse.success("Event image uploaded successfully", Map.of("url", url)));
    }

    @PostMapping("/notes")
    @Operation(summary = "Upload notes file")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadNotes(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file, "campusnexus/notes", "raw");
        return ResponseEntity.ok(ApiResponse.success("Notes file uploaded successfully", Map.of("url", url)));
    }

    @PostMapping("/submission")
    @Operation(summary = "Upload submission file")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadSubmission(@RequestParam("file") MultipartFile file) {
        String resourceType = detectResourceType(file);
        String url = cloudinaryService.uploadFile(file, "campusnexus/submissions", resourceType);
        return ResponseEntity.ok(ApiResponse.success("Submission file uploaded successfully", Map.of("url", url)));
    }

    private String detectResourceType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif") || lower.endsWith(".webp")) {
                return "image";
            }
        }
        return "raw";
    }
}
