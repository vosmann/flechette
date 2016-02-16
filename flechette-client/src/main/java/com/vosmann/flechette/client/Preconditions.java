package com.vosmann.flechette.client;

public class Preconditions {

    public static long checkMin(final long argument, final long min, final String message) {
        if(argument < min) {
            throw new IllegalArgumentException(message);
        }

        return argument;
    }

    public static int checkMin(final int argument, final int min, final String message) {
        if(argument < min) {
            throw new IllegalArgumentException(message);
        }

        return argument;
    }

}
