package com.sparta.deliverit.ai.presentation.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GeminiRequestDto {

    private List<Content> contents;

    public static GeminiRequestDto of(String text) {
        List<Content> contentList = List.of(
                new Content(
                        List.of(new Part(text))
                ));
        return new GeminiRequestDto(contentList);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content{
        private List<Part> parts;

    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part{
        private String text;
    }
}
