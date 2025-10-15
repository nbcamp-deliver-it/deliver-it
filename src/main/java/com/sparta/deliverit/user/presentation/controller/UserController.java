package com.sparta.deliverit.user.presentation.controller;

import com.sparta.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.sparta.deliverit.global.presentation.dto.Result;
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

import static com.sparta.deliverit.global.response.code.UserResponseCode.*;

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
    public Result<UserResponseDto> getUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        log.info("=== 회원 조회 userId : {} ===", userId);
        UserInfo userInfo = userService.getUserInfo(userId);
        log.info("=== 회원 조회 완료 ===");
        return Result.of(
                USER_QUERY_SUCCESS.getMessage(),
                USER_QUERY_SUCCESS.name(),
                UserResponseDto.from(userInfo)
        );
    }

    @DeleteMapping("/users")
    public Result<Long> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        log.info("=== 회원 삭제 userId : {} ===", userId);
        Long deletedUserId = userService.deleteUser(userId);
        log.info("=== 회원 삭제 완료 ===");
        return Result.of(
                USER_DELETE_SUCCESS.getMessage(),
                USER_DELETE_SUCCESS.name(),
                deletedUserId
        );
    }
}
