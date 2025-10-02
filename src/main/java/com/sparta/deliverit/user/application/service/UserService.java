package com.sparta.deliverit.user.application.service;


import com.sparta.deliverit.user.domain.entity.User;
import com.sparta.deliverit.user.domain.entity.UserRoleEnum;
import com.sparta.deliverit.user.domain.repository.UserRepository;
import com.sparta.deliverit.user.presentation.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("ID 중복");
        }


        String phone = requestDto.getPhone();
        Optional<User> checkPhone = userRepository.findByPhone(phone);
        if (checkPhone.isPresent()) {
            throw new IllegalArgumentException("전화번호 중복.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호 에러");
            }
            role = UserRoleEnum.ADMIN;
        }

        String name = requestDto.getName();

        // 사용자 등록
        User user = new User(username, password, name , phone, role);
        userRepository.save(user);
    }
}