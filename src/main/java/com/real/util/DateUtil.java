package com.real.util;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date Utility class
 */
public final class DateUtil {
    private DateUtil() {
    }

    /**
     * Parse date from string based on pattern argument
     *
     * @param date - String representation of DateTime
     * @param pattern - DateTime pattern
     *
     * @return LocalDateTime
     */
    public static LocalDateTime parse(@NonNull final String date, @NonNull final String pattern) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parse date from string based on pattern and return YEAR of Date
     *
     * @param date - String representation of DateTime
     * @param pattern - DateTime pattern
     *
     * @return String - Year of Date
     */
    public static String getDateYear(@NonNull final String date, @NonNull final String pattern) {
        return String.valueOf(LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern)).getYear());
    }
}
