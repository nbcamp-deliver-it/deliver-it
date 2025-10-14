package com.sparta.deliverit.ai.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.ai.application.AiMenuDescriptionService;
import com.sparta.deliverit.ai.presentation.dto.GeminiRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

@RestController
@Slf4j
@RequestMapping("/v1")
public class AiMenuDescriptionController {

    private final URI geminiURI = java.net.URI.create(
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent");
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AiMenuDescriptionService service;

    public AiMenuDescriptionController(@Qualifier("geminiClient") RestTemplate restTemplate,
                                       ObjectMapper objectMapper,
                                       AiMenuDescriptionService service) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @PostMapping("/ai/request")
    public String googleRt(@RequestBody GeminiRequestDto dto) throws IOException {

        RequestEntity<GeminiRequestDto> requestEntity = new RequestEntity<>(dto, HttpMethod.POST, geminiURI);
        ResponseEntity<String> result = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = objectMapper.readTree(result.getBody());
        JsonNode resultBody = jsonNode.findValue("text");

        return resultBody.asText();
    }

    @PostMapping("/ai/request/service")
    public String serviceRT(@RequestParam String question) {
        log.info("serviceRT");
        return service.askQuestionToAi(question);
    }
}
