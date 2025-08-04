package com.weather.exception;

import org.springframework.http.HttpStatus;

public class WeatherServiceException extends WeatherAppException {
    public WeatherServiceException(String message) {
        super("SERVICE_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
