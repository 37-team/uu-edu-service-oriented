package com.bigitcompany.cloudaireadmodel.common.domain.services;

public class TimerService {

    private TimerService() {
        // private constructor to prevent instantiating a class with only static methods
    }

    /**
     * Returns the current nanoTime.
     * Intended to be used for measuring process duration.
     */
    public static long start() {
        return System.nanoTime();
    }

    /**
     * Takes the start nanoTime of a process and returns the difference between then and now.
     */
    public static long stopAndDiff(long startTime) {
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Takes the nanoTime duration of a process and returns a string containing the duration in milliseconds.
     */
    public static String formatExecutionTime(long duration) {
        return (duration / 1_000_000.0) + "ms";
    }
}