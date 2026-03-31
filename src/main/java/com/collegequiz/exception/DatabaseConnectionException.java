package com.collegequiz.exception;

public class DatabaseConnectionException extends QuizException {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
