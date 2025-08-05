package com.weather.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends WeatherAppException {
    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
