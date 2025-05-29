package com.example.dailymate.utils;

public class inputValidator {
    public static boolean isValidInput(String title, String description) {
        return !(title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty());
    }
}
