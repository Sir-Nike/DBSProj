package com.collegequiz.model;

import java.time.LocalDateTime;

public record AvailableQuizRow(
        Integer quizId,
        String quizTitle,
        String subjectCode,
        String subjectName,
        Integer durationMinutes,
        Double totalMarks,
        LocalDateTime quizDate,
        String resultsPublished,
        Integer subjectId
) {
    @Override
    public String toString() {
        return quizId + " - " + quizTitle;
    }
}
