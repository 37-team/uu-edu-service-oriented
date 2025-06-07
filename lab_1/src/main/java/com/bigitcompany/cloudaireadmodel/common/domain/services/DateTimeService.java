package com.bigitcompany.cloudaireadmodel.common.domain.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class DateTimeService {

    private static final String PATTERN = "yyyy-MM-dd";

    private static final DateTimeFormatter noMillisecondsFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(java.time.ZoneId.of("UTC"));

    public static final DateTimeFormatter START_DATE_FORMATTER = new DateTimeFormatterBuilder()
        .parseLenient()
        .appendPattern(PATTERN)
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
        .toFormatter();

    public static final DateTimeFormatter END_DATE_FORMATTER = new DateTimeFormatterBuilder()
        .parseLenient()
        .appendPattern(PATTERN)
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
        .toFormatter();

    private DateTimeService(){

    }

    /**
     * Similar to Instant.parse but returns null instead of throwing an exception if null input is provided.
     * */
    public static Instant toInstant(String datetime) {
        if (datetime == null) {
            return null;
        }
        return Instant.parse(datetime);
    }

    /**
     * Converts a timestamp to instant, returning null instead of throwing an exception if null input is provided.
     * */
    public static Instant toInstant(Timestamp datetime) {
        if (datetime == null) {
            return null;
        }
        return datetime.toInstant();
    }

    /**
     * Converts a String to a java.util.Date.
     * */
    public static Date toDate(String datetime) {
        if (datetime == null) {
            return null;
        }
        return Date.from(Instant.parse(datetime));
    }

    /**
     * Similar to ZonedDateTime.parse but returns null instead of throwing an exception if null input is provided.
     * */
    public static ZonedDateTime toZonedDateTime(String datetime) {
        if (datetime == null) {
            return null;
        }
        return ZonedDateTime.parse(datetime);
    }

    /**
     * Obtains an instance of Instant from a text string such as 2022-02-04.
     */
    public static Instant fromStringDateToInstant(String date, DateTimeFormatter formatter) {
        if (date == null) {
            return null;
        }
        return Timestamp.valueOf(LocalDateTime.parse(date, formatter)).toInstant();
    }

    /**
     * Creates a string in the format yyyy-MM-dd from an Instant.
     */

    public static String fromInstantToStringDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.toString().substring(0, 10);
    }

    /**
     * Creates a string in the format yyyy-MM-ddTHH-mm-ssZ from an instant. Without milliseconds
     */
    public static String fromInstantToISOStringNoMilliseconds(Instant instant) {
        if (instant == null) {
            return null;
        }
        return noMillisecondsFormatter.format(instant);
    }

    public static LocalTime fromStringTimeToLocalTime(String time) {
        if (time == null) {
            return null;
        }
        return LocalTime.parse(time);
    }

    public static Instant fromIsoDateStringToInstantStartOfTheDay(String date) {
        if (date == null) {
            return null;
        }
        return Timestamp.valueOf(
                LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
        ).toInstant();
    }

    public static Instant fromIsoDateStringToInstantEndOfTheDay(String date) {
        if (date == null) {
            return null;
        }
        return Timestamp.valueOf(
                LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59)
        ).toInstant();
    }

    public static String instantToString(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.toString();
    }

}