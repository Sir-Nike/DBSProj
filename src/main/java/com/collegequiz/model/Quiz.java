package com.collegequiz.model;

import java.time.LocalDateTime;

public record Quiz(
        Integer quizId,
        String quizTitle,
        Integer durationMinutes,
        Double totalMarks,
        LocalDateTime quizDate,
        String resultsPublished,
        Integer subjectId,
        Integer createdBy
) {
    @Override
    public String toString() {
        return quizDate == null ? quizTitle : quizTitle + " \u2022 " + quizDate.toLocalDate();
    }
}
