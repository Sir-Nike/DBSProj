package com.collegequiz.model;

public record QuestionOption(
        Integer optionId,
        String optionText,
        String isCorrect,
        Integer questionId,
        Integer displayOrder
) {
    @Override
    public String toString() {
        return optionText;
    }
}
