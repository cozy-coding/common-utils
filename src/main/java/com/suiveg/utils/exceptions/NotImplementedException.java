package com.suiveg.utils.exceptions;

public class NotImplementedException extends Exception {

    public static final String METHOD_NOT_IMPLEMENTED_MESSAGE = "Method not implemented yet.";

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException() {
        super();
    }
}
