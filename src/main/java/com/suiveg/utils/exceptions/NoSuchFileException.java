package com.suiveg.utils.exceptions;

public class NoSuchFileException extends Exception {

    public static final String NO_SUCH_FILE_EXCEPTION = "File not found. No such file Exception.";

    public NoSuchFileException(String message) {
        super(message);
    }

    public NoSuchFileException() {
        super();
    }
}
