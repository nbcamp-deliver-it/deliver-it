package com.sparta.deliverit.review.presentation.dto.response;

import java.util.List;

public record ReviewListResponse(
        List<ReviewResponse> list
) {
}
