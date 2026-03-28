package dev.esanchez.timely.backend.util;

import java.time.LocalTime;

public final class ValidationUtils {

    private ValidationUtils() {
        // evita instanciación
    }

    //Checks if the text is null or empty, and then trim the text
    public static String validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be null or blank");
        }
        return value.trim();
    }

    //Checks if the object is null
    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public static void validateTimeRange(LocalTime start, LocalTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}