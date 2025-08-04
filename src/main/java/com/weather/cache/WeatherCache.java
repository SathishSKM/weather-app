package com.weather.cache;

import com.weather.dto.WeatherResponseDTO;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherCache {

    private final Map<String, WeatherResponseDTO> cache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastUpdated = new ConcurrentHashMap<>();
    private static final Duration CACHE_DURATION = Duration.ofHours(1);

    public Optional<WeatherResponseDTO> getCachedForecast(String city) {
        if (cache.containsKey(city) &&
                lastUpdated.containsKey(city) &&
                lastUpdated.get(city).plus(CACHE_DURATION).isAfter(LocalDateTime.now())) {
            return Optional.of(cache.get(city));
        }
        return Optional.empty();
    }

    public void cacheForecast(String city, WeatherResponseDTO forecast) {
        cache.put(city, forecast);
        lastUpdated.put(city, LocalDateTime.now());
    }
}
