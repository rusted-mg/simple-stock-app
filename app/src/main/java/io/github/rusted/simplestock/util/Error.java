package io.github.rusted.simplestock.util;

public class Error {
    public final String message;
    public final Throwable exception;

    public Error(String message, Throwable exception) {
        this.message = message;
        this.exception = exception;
    }
}