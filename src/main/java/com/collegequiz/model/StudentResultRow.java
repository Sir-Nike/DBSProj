package com.collegequiz.model;

import java.time.LocalDateTime;

public record StudentResultRow(
        Integer attemptId,
        Integer studentId,
        String registrationNo,
        String studentName,
        Integer quizId,
        String quizTitle,
        String subjectCode,
        String subjectName,
        Double totalScore,
        Double totalMarks,
        Double percentage,
        LocalDateTime submissionTime
) {
}
