package com.sparta.deliverit.restaurant.infrastructure.api.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddressResponseDto {
    private List<Document> documents;

    @Data
    public static class Document {
        @JsonProperty("x")
        private String x; // longitude (경도)

        @JsonProperty("y")
        private String y; // latitude (위도)
    }
}
