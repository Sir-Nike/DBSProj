package com.collegequiz.model;

public record Teacher(
        Integer teacherId,
        String teacherCode,
        String name,
        Integer departmentId
) {
    @Override
    public String toString() {
        return teacherCode + " - " + name;
    }
}
