package com.collegequiz.controller;

public final class ThemeManager {
    public enum Theme {
        LIGHT,
        DARK
    }

    private static Theme currentTheme = Theme.LIGHT;

    private ThemeManager() {
    }

    public static Theme toggle() {
        currentTheme = currentTheme == Theme.LIGHT ? Theme.DARK : Theme.LIGHT;
        return currentTheme;
    }

    public static String getThemeStylesheet() {
        return switch (currentTheme) {
            case LIGHT -> "/com/collegequiz/css/theme-light.css";
            case DARK -> "/com/collegequiz/css/theme-dark.css";
        };
    }

}
