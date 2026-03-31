package com.collegequiz.model;

public record Question(
        Integer questionId,
        String questionText,
        String questionType,
        Double marks,
        Integer quizId,
        Integer displayOrder
) {
    @Override
    public String toString() {
        return displayOrder + ". " + questionText;
    }
}
