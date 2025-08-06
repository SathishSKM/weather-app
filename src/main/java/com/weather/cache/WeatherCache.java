package com.weather.cache;

import com.weather.dto.WeatherResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WeatherCache {

    @Cacheable(value = "weather", key = "#city")
    public WeatherResponseDTO getCachedForecast(String city) {
        log.info("Cached weather data not found for city: {}", city);
        return null;
    }

    @CachePut(value = "weather", key = "#city")
    public WeatherResponseDTO cacheForecast(String city, WeatherResponseDTO forecast) {
        return forecast;
    }

    @Cacheable(value = "openweather", key = "#city")
    public OpenWeatherMapResponseDTO getCachedOpenForecast(String city) {
        log.info("Cached weather data not found for city: {}", city);
        return null;
    }

    @CachePut(value = "openweather", key = "#city")
    public OpenWeatherMapResponseDTO cacheOpenForecast(String city, OpenWeatherMapResponseDTO forecast) {
        return forecast;
}
