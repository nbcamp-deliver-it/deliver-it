package com.sparta.deliverit.user.presentation.controller;

import com.sparta.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.sparta.deliverit.global.response.ApiResponse;
import com.sparta.deliverit.global.response.code.UserResponseCode;
import com.sparta.deliverit.user.application.service.UserService;
import com.sparta.deliverit.user.application.service.dto.UserInfo;
import com.sparta.deliverit.user.presentation.dto.SignupRequestDto;
import com.sparta.deliverit.user.presentation.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/user/signup",
            consumes = "application/json",
            produces = "text/plain; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return  ResponseEntity.badRequest().body("가입에러");
        }

        userService.signup(requestDto);

        return ResponseEntity.ok(requestDto.getName() + "님 가입을 환영합니다.");
    }

    @GetMapping("/users/profile")
    public ApiResponse<UserResponseDto> getUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        log.info("=== 유저 조회 userId : {} ===", userId);
        UserInfo userInfo = userService.getUserInfo(userId);
        log.info("=== 유저 조회 성공 ===");
        return ApiResponse.create(
                UserResponseCode.USER_QUERY_SUCCESS,
                UserResponseCode.USER_QUERY_SUCCESS.getMessage(),
                UserResponseDto.from(userInfo)
        );
    }
}
