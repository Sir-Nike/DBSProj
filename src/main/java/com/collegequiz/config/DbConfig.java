package com.collegequiz.config;

public final class DbConfig {
    private DbConfig() {
    }

    public static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    public static final String DEFAULT_USER = "system";
    public static final String DEFAULT_PASSWORD = "Hello123!";

    public static String url() {
        return System.getProperty("quiz.db.url", System.getenv().getOrDefault("QUIZ_DB_URL", DEFAULT_URL));
    }

    public static String user() {
        return System.getProperty("quiz.db.user", System.getenv().getOrDefault("QUIZ_DB_USER", DEFAULT_USER));
    }

    public static String password() {
        return System.getProperty("quiz.db.password", System.getenv().getOrDefault("QUIZ_DB_PASSWORD", DEFAULT_PASSWORD));
    }
}
