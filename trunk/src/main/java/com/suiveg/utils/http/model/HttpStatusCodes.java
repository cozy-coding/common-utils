package com.suiveg.utils.http.model;

public enum HttpStatusCodes {
    OK_FOUND(200);

    private int statusCode;

    HttpStatusCodes(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getCode() {
        return statusCode;
    }
}
