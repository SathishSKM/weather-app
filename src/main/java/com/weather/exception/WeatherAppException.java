package com.weather.exception;

import org.springframework.http.HttpStatus;

public class WeatherAppException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    public WeatherAppException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    // Getters
    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
