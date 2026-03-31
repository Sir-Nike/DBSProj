package com.collegequiz.controller;

public final class QuizRuntimeContext {
    private static Integer quizId;
    private static Integer attemptId;

    private QuizRuntimeContext() {
    }

    public static Integer getQuizId() {
        return quizId;
    }

    public static void setQuizId(Integer quizId) {
        QuizRuntimeContext.quizId = quizId;
    }

    public static Integer getAttemptId() {
        return attemptId;
    }

    public static void setAttemptId(Integer attemptId) {
        QuizRuntimeContext.attemptId = attemptId;
    }

    public static void clear() {
        quizId = null;
        attemptId = null;
    }
}
