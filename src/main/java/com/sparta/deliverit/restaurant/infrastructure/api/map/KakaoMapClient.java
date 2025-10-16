package com.sparta.deliverit.restaurant.infrastructure.api.map;

import com.sparta.deliverit.global.exception.MapException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.sparta.deliverit.global.response.code.MapResponseCode.GEOCODING_API_ERROR;
import static com.sparta.deliverit.global.response.code.MapResponseCode.GEOCODING_API_TIMEOUT;

@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    private final RestTemplate kakaoRestTemplate;

    // 주소 -> 좌표 (반환 타입: AddressResponseDto)
    public AddressResponseDto geocode(String address) {
        try {
            return kakaoRestTemplate.getForObject(
                    "/v2/local/search/address.json?query={query}",
                    AddressResponseDto.class,
                    address
            );
        } catch (ResourceAccessException e) {
            throw new MapException(GEOCODING_API_TIMEOUT);
        } catch (RestClientException e) {
            throw new MapException(GEOCODING_API_ERROR);
        }
    }
}
