package com.weather.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends WeatherAppException {
    public BadRequestException(String message) {
        super("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }
}
