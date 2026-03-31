package com.collegequiz.model;

public record Student(
        Integer studentId,
        String registrationNo,
        String name,
        Integer departmentId
) {
    @Override
    public String toString() {
        return name + " (" + registrationNo + ")";
    }
}
