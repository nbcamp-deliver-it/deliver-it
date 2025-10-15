package com.sparta.deliverit.user.presentation.dto;

import com.sparta.deliverit.user.application.service.dto.UserInfo;

public record UserResponseDto(
        String name,
        String phoneNumber,
        String role
) {
    public static UserResponseDto from(UserInfo userInfo) {
        return new UserResponseDto(
                userInfo.name(),
                userInfo.phoneNumber(),
                userInfo.role()
        );
    }
}
