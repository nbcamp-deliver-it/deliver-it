package com.sparta.deliverit.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String name;
    @NotBlank
    private String phone;
    private boolean admin = false;
    private String adminToken = "";
}