package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.InvalidatedToken;
import com.campusnexus.entity.User;
import com.campusnexus.enums.Role;
import com.campusnexus.exception.BadRequestException;
import com.campusnexus.exception.DuplicateResourceException;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.CollegeRepository;
import com.campusnexus.repository.DepartmentRepository;
import com.campusnexus.repository.InvalidatedTokenRepository;
import com.campusnexus.repository.UserRepository;
import com.campusnexus.security.JwtUtil;
import com.campusnexus.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthServiceImpl(UserRepository userRepository,
            CollegeRepository collegeRepository,
            DepartmentRepository departmentRepository,
            InvalidatedTokenRepository invalidatedTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (request.getRole() == Role.CAMPUS_ADMIN) {
            throw new BadRequestException("Cannot register with CAMPUS_ADMIN role");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .isActive(true)
                .build();

        if (request.getCollegeId() != null) {
            user.setCollege(collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found")));
        }

        if (request.getDepartmentId() != null) {
            user.setDepartment(departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        }

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (invalidatedTokenRepository.existsByToken(refreshToken)) {
            throw new BadRequestException("Refresh token has been invalidated");
        }

        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new BadRequestException("Refresh token has expired");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        // Invalidate old refresh token
        InvalidatedToken invalidated = InvalidatedToken.builder()
                .token(refreshToken)
                .build();
        invalidatedTokenRepository.save(invalidated);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .role(user.getRole())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null && !invalidatedTokenRepository.existsByToken(token)) {
            InvalidatedToken invalidated = InvalidatedToken.builder()
                    .token(token)
                    .build();
            invalidatedTokenRepository.save(invalidated);
        }
    }
}
