package com.example.submerge.calendar.exceptions;

/**
 * Created by Mateusz Kornakiewicz on 27.10.2017.
 */

public class OutOfDateRangeException extends Exception {
    public OutOfDateRangeException(String message) {
        super(message);
    }
}
