package com.collegequiz.model;

public record Subject(
        Integer subjectId,
        String subjectName,
        String subjectCode,
        Integer semester,
        Integer departmentId
) {
    @Override
    public String toString() {
        return subjectCode + " - " + subjectName;
    }
}
