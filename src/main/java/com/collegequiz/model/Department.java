package com.collegequiz.model;

public record Department(
        Integer departmentId,
        String departmentName
) {
    @Override
    public String toString() {
        return departmentName;
    }
}
