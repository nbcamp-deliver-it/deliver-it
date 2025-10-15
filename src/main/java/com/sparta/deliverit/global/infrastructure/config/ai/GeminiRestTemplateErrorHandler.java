package com.sparta.deliverit.global.infrastructure.config.ai;

import com.sparta.deliverit.global.exception.AiException;
import com.sparta.deliverit.global.response.code.AiResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Component
public class GeminiRestTemplateErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response)  {
        HttpStatusCode statusCode;
        try {
            statusCode = response.getStatusCode();
        } catch (IOException e) {
            throw new AiException(AiResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (statusCode.is4xxClientError()) {
            throw new AiException(AiResponseCode.INPUT_DATA_ERROR);
        } else if (statusCode.is5xxServerError()) {
            throw new AiException(AiResponseCode.AI_SERVER_ERROR);
        }
    }
}
