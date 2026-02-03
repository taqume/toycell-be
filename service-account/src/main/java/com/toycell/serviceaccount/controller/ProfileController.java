package com.toycell.serviceaccount.controller;

import com.toycell.commondomain.response.ApiResponse;
import com.toycell.serviceaccount.dto.CreateProfileRequest;
import com.toycell.serviceaccount.dto.ProfileResponse;
import com.toycell.serviceaccount.dto.UpdateProfileRequest;
import com.toycell.serviceaccount.security.JwtTokenValidator;
import com.toycell.serviceaccount.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final JwtTokenValidator jwtTokenValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProfileResponse> createProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateProfileRequest request) {
        
        Long userId = extractUserIdFromToken(authHeader);
        log.info("POST /api/profiles - UserId: {}", userId);
        
        ProfileResponse response = profileService.createProfile(userId, request);
        return ApiResponse.success("Profile created successfully", response);
    }

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        log.info("GET /api/profiles/me - UserId: {}", userId);
        
        ProfileResponse response = profileService.getProfileByUserId(userId);
        return ApiResponse.success(response);
    }

    @PutMapping("/me")
    public ApiResponse<ProfileResponse> updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        Long userId = extractUserIdFromToken(authHeader);
        log.info("PUT /api/profiles/me - UserId: {}", userId);
        
        ProfileResponse response = profileService.updateProfile(userId, request);
        return ApiResponse.success("Profile updated successfully", response);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("Account Service is running");
    }

    private Long extractUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenValidator.getUserIdFromToken(token);
    }
}
