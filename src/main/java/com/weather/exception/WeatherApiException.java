package com.weather.exception;

import org.springframework.http.HttpStatus;

public class WeatherApiException extends WeatherAppException {
    public WeatherApiException(String message) {
        super("WEATHER_API_ERROR", message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
