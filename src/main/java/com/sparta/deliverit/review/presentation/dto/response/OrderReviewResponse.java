package com.sparta.deliverit.review.presentation.dto.response;

import java.util.List;

public record OrderReviewResponse(
        List<ReviewResponse> list
) {
}
