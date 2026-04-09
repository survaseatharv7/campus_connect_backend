package com.campusnexus.controller;

import com.campusnexus.dto.*;
import com.campusnexus.entity.College;
import com.campusnexus.entity.Department;
import com.campusnexus.repository.CollegeRepository;
import com.campusnexus.repository.DepartmentRepository;
import com.campusnexus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService authService;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;

    public AuthController(AuthService authService,
                          CollegeRepository collegeRepository,
                          DepartmentRepository departmentRepository) {
        this.authService = authService;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user with any role except CAMPUS_ADMIN")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and get JWT tokens")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get a new access token using refresh token")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refresh token")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate current token")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logged out successfully")
    })
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @GetMapping("/colleges")
    @Operation(summary = "Get all active colleges", description = "Public endpoint to fetch colleges for registration dropdown")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Colleges retrieved")
    })
    public ResponseEntity<ApiResponse<List<CollegeDropdownResponse>>> getColleges() {
        List<CollegeDropdownResponse> colleges = collegeRepository.findAll()
                .stream()
                .filter(c -> c.getStatus() == com.campusnexus.enums.CollegeStatus.ACTIVE)
                .map(c -> CollegeDropdownResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .city(c.getCity())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Colleges retrieved", colleges));
    }

    @GetMapping("/departments/{collegeId}")
    @Operation(summary = "Get departments by college", description = "Public endpoint to fetch departments for registration dropdown")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Departments retrieved")
    })
    public ResponseEntity<ApiResponse<List<DepartmentDropdownResponse>>> getDepartmentsByCollege(
            @PathVariable UUID collegeId) {
        List<DepartmentDropdownResponse> departments = departmentRepository.findByCollegeId(collegeId)
                .stream()
                .map(d -> DepartmentDropdownResponse.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .code(d.getCode())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved", departments));
    }
}