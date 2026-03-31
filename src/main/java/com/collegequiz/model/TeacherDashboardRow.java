package com.collegequiz.model;

import java.time.LocalDateTime;

public record TeacherDashboardRow(
        Integer teacherId,
        String teacherName,
        String departmentName,
        Integer subjectId,
        String subjectCode,
        String subjectName,
        Integer quizId,
        String quizTitle,
        LocalDateTime quizDate,
        Integer durationMinutes,
        Double totalMarks,
        String resultsPublished,
        Integer questionCount,
        Integer attemptCount,
        Integer submittedCount,
        Double avgScore,
        Double topScore
) {
}
