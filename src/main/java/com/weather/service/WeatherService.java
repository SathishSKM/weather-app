package com.weather.service;

import com.weather.cache.WeatherCache;
import com.weather.client.OpenWeatherMapClient;
import com.weather.dto.DailyForecast;
import com.weather.dto.openweathermap.Forecast;
import com.weather.dto.openweathermap.OpenWeatherMapResponseDTO;
import com.weather.dto.WeatherResponseDTO;
import com.weather.exception.WeatherServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherService {

    private final OpenWeatherMapClient openWeatherMapClient;
    private final WeatherCache weatherCache;

    @Autowired
    public WeatherService(OpenWeatherMapClient openWeatherMapClient, WeatherCache weatherCache) {
        this.openWeatherMapClient = openWeatherMapClient;
        this.weatherCache = weatherCache;
    }

    public WeatherResponseDTO getForecast(String city, boolean offlineMode) {
        log.info("Fetching weather forecast for city: {}, offline mode: {}", city, offlineMode);
        if (offlineMode) {
            log.info("Fetching cached weather data for city: {}", city);
            WeatherResponseDTO cachedForecast = weatherCache.getCachedForecast(city);
            if (cachedForecast == null) {
                throw new WeatherServiceException("No cached data available for offline mode");
            }
            return cachedForecast;
        }

        try {
            OpenWeatherMapResponseDTO openWeatherMapResponse = openWeatherMapClient.getForecast(city);
            WeatherResponseDTO processedWeatherResponse = processWeatherData(openWeatherMapResponse);
            weatherCache.cacheForecast(city, processedWeatherResponse);
            log.info("Weather data processed and cached for city: {}", city);
            return processedWeatherResponse;
        } catch (Exception ex) {
            WeatherResponseDTO cachedForecast = weatherCache.getCachedForecast(city);
            log.error("Error fetching weather data for city: {}, error: {}", city, ex.getMessage());
            if (cachedForecast != null) {
                return cachedForecast;
            }
            throw ex;
        }
    }

    private WeatherResponseDTO processWeatherData(OpenWeatherMapResponseDTO openWeatherMapResponse) {
        List<DailyForecast> forecasts = new ArrayList<>();

        Map<LocalDate, List<Forecast>> forecastsByDate = openWeatherMapResponse.getForecasts().stream()
                .collect(Collectors.groupingBy(
                        forecast -> LocalDate.parse(forecast.getDtText().split(" ")[0]),
                        TreeMap::new,
                        Collectors.toList()));

        forecastsByDate.entrySet().stream()
                .limit(3)
                .forEach(entry -> {
                    DailyForecast daily = processDailyForecast(entry.getKey(), entry.getValue());
                    forecasts.add(daily);
                });

        return new WeatherResponseDTO(openWeatherMapResponse.getCity().getName(), forecasts);
    }

    private DailyForecast processDailyForecast(LocalDate date, List<Forecast> forecasts) {
        double maxTemp = forecasts.stream()
                .mapToDouble(forecast -> forecast.getMain().getTempMax())
                .max()
                .orElse(0);

        double minTemp = forecasts.stream()
                .mapToDouble(forecast -> forecast.getMain().getTempMin())
                .min()
                .orElse(0);

        boolean hasRain = forecasts.stream()
                .anyMatch(forecast -> forecast.getWeather().stream()
                        .anyMatch(weather -> "Rain".equalsIgnoreCase(weather.getMain())));

        boolean hasThunderstorm = forecasts.stream()
                .anyMatch(forecast -> forecast.getWeather().stream()
                        .anyMatch(weather -> "Thunderstorm".equalsIgnoreCase(weather.getMain())));

        double maxWind = forecasts.stream()
                .mapToDouble(f -> f.getWind().getSpeed())
                .max()
                .orElse(0);

        List<String> alerts = new ArrayList<>();
        if (hasRain) alerts.add("Carry umbrella");
        if (maxTemp > 40) alerts.add("Use sunscreen lotion");
        if (maxWind > 10) alerts.add("It's too windy, watch out!");
        if (hasThunderstorm) alerts.add("Don't step out! A Storm is brewing!");

        double maxTempCelsius = maxTemp - 273.15;
        double minTempCelsius = minTemp - 273.15;

        return new DailyForecast(date, maxTempCelsius, minTempCelsius, alerts);
    }
}