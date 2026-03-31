package com.collegequiz.model;

import java.time.LocalDateTime;

public record StudentAnswer(
        Integer answerId,
        String isCorrect,
        Integer attemptId,
        Integer questionId,
        Integer selectedOptionId,
        LocalDateTime lastUpdatedAt
) {
}
