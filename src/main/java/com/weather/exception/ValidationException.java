package com.weather.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends WeatherAppException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
