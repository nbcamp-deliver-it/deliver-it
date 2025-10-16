package com.sparta.deliverit.restaurant.infrastructure.api.map;

import com.sparta.deliverit.global.exception.MapException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static com.sparta.deliverit.global.response.code.MapResponseCode.ADDRESS_GEOCODING_FAILED;
import static com.sparta.deliverit.global.response.code.MapResponseCode.INVALID_ADDRESS_INPUT;

@Service
@RequiredArgsConstructor
public class MapService {

    private final KakaoMapClient kakaoMapClient;

    // 주소 -> 좌표 (반환 타입: Coordinates)
    public Coordinates geocode(String address) {
        // 좌표로 변환할 수 없는 주소
        if (!StringUtils.hasText(address))
            throw new MapException(INVALID_ADDRESS_INPUT);

        AddressResponseDto response = kakaoMapClient.geocode(address);

        // 주소는 정상이지만 좌표로 변환 불가
        if (response == null || CollectionUtils.isEmpty(response.getDocuments())) {
            throw new MapException(ADDRESS_GEOCODING_FAILED);
        }

        AddressResponseDto.Document doc = response.getDocuments().get(0);
        return new Coordinates(doc.getLongitude(), doc.getLatitude());
    }
}
