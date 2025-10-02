package com.sparta.deliverit.user.application.service;


import com.sparta.deliverit.user.domain.entity.User;
import com.sparta.deliverit.user.domain.entity.UserRoleEnum;
import com.sparta.deliverit.user.domain.repository.UserRepository;
import com.sparta.deliverit.user.presentation.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    private static final Map<String, UserRoleEnum> ROLE_ALIASES = buildRoleAliases();

    private static Map<String, UserRoleEnum> buildRoleAliases() {
        Map<String, UserRoleEnum> m = new HashMap<>();
        for (UserRoleEnum r : UserRoleEnum.values()) {
            m.put(r.name(), r);
            m.put(r.getAuthority(), r);
        }
        return m;
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().toUpperCase(Locale.ROOT);
    }

    private static UserRoleEnum resolveRole(String input) {
        String key = normalize(input);
        if (key == null || key.isEmpty()) return UserRoleEnum.CUSTOMER; // 기본값
        UserRoleEnum r = ROLE_ALIASES.get(key);
        if (r == null) {
            throw new IllegalArgumentException("지원하지 않는 권한입니다: " + input);
        }
        return r;
    }

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

        UserRoleEnum requestedRole = resolveRole(requestDto.getRole());

        // 2) 역할별 검증/분기
        switch (requestedRole) {
            case MASTER, MANAGER -> {
                if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                    throw new IllegalArgumentException("관리자 토큰 불일치");
                }
            }
            case OWNER -> {

            }
            case CUSTOMER -> {

            }
        }

        //중복 체크 필요 없음
        String name = requestDto.getName();

        User user = new User(username, password, name , phone, requestedRole);
        userRepository.save(user);
    }
}