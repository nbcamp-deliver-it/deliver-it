package com.sparta.deliverit.restaurant.infrastructure.api.map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapService {

    private final KakaoMapClient kakaoMapClient;

    // String -> Double
    public Coordinates geocode(String address) {
        AddressResponseDto response = kakaoMapClient.geocode(address);

        if (response.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("주소를 변환할 수 없습니다: " + address);
        }

        AddressResponseDto.Document doc = response.getDocuments().get(0);
        return new Coordinates(
                Double.parseDouble(doc.getX()),
                Double.parseDouble(doc.getY())
        );
    }
}
