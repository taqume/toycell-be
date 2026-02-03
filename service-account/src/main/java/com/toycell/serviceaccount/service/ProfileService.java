package com.toycell.serviceaccount.service;

import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commonexception.exception.BusinessException;
import com.toycell.serviceaccount.dto.CreateProfileRequest;
import com.toycell.serviceaccount.dto.ProfileResponse;
import com.toycell.serviceaccount.dto.UpdateProfileRequest;
import com.toycell.serviceaccount.entity.UserProfile;
import com.toycell.serviceaccount.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository profileRepository;

    @Transactional
    public ProfileResponse createProfile(Long userId, CreateProfileRequest request) {
        log.info("Creating profile for userId: {}", userId);

        // Check if profile already exists
        if (profileRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.PROFILE_ALREADY_EXISTS);
        }

        // Create new profile
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .identityNumber(request.getIdentityNumber()) // Will be encrypted by converter
                .phoneNumber(request.getPhoneNumber()) // Will be encrypted by converter
                .birthDate(request.getBirthDate())
                .verified(false)
                .build();

        profile = profileRepository.save(profile);
        log.info("Profile created successfully for userId: {}", userId);

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUserId(Long userId) {
        log.info("Fetching profile for userId: {}", userId);

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        return mapToResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for userId: {}", userId);

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        // Update only non-null fields
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBirthDate() != null) {
            profile.setBirthDate(request.getBirthDate());
        }

        profile = profileRepository.save(profile);
        log.info("Profile updated successfully for userId: {}", userId);

        return mapToResponse(profile);
    }

    private ProfileResponse mapToResponse(UserProfile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .identityNumber(profile.getIdentityNumber()) // Decrypted by converter
                .phoneNumber(profile.getPhoneNumber()) // Decrypted by converter
                .birthDate(profile.getBirthDate())
                .verified(profile.getVerified())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
