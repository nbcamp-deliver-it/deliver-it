package com.sparta.deliverit.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.ai.domain.entity.AiMenuDescription;
import com.sparta.deliverit.ai.domain.repository.AiMenuDescriptionRepository;
import com.sparta.deliverit.ai.presentation.dto.GeminiRequestDto;
import com.sparta.deliverit.global.exception.AiException;
import com.sparta.deliverit.global.response.code.AiResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
@Transactional
public class GeminiMenuDescriptionServiceImpl implements AiMenuDescriptionService{

    private final URI geminiURI = java.net.URI.create("dummyURL");
    private final AiMenuDescriptionRepository aiMenuDescriptionRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public GeminiMenuDescriptionServiceImpl(AiMenuDescriptionRepository aiMenuDescriptionRepository,
                                            ObjectMapper objectMapper,
                                            @Qualifier("geminiClient") RestTemplate restTemplate) {
        this.aiMenuDescriptionRepository = aiMenuDescriptionRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public String askQuestionToAi(String question) {
        log.info("service");
        GeminiRequestDto geminiRequestDto = GeminiRequestDto.of(question);

        RequestEntity<GeminiRequestDto> requestEntity =
                new RequestEntity<>(geminiRequestDto, HttpMethod.POST, geminiURI);

        ResponseEntity<String> QuestionResult = restTemplate.exchange(requestEntity, String.class);

        String result = parseJsonToString(QuestionResult);
        aiMenuDescriptionRepository.save(AiMenuDescription.of(question, result));

        return result;
    }

    private String parseJsonToString(ResponseEntity<String> QuestionResult) {
        log.info("parseJsonToString");
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(QuestionResult.getBody());
        } catch (Exception e) {
            throw new AiException(AiResponseCode.INTERNAL_SERVER_ERROR);
        }

        JsonNode resultBody = jsonNode.findValue("text");
        String result = resultBody.asText();

        if(StringUtils.hasText(result))
            return result;
        else throw new AiException(AiResponseCode.INTERNAL_SERVER_ERROR);
    }
}
