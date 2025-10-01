package com.sparta.deliverit.restaurant.infrastructure.api.map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    private final RestTemplate kakaoRestTemplate;

    // 주소 -> 좌표 (JSON 형식의 데이터를 DTO(String)로 받아옴)
    public AddressResponseDto geocode(String address) {
        return kakaoRestTemplate.getForObject(
                "/v2/local/search/address.json?query={query}",
                AddressResponseDto.class,
                address
        );
    }
}
