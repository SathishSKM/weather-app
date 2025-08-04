package com.weather.service;

import com.weather.cache.WeatherCache;
import com.weather.client.OpenWeatherMapClient;
import com.weather.dto.DailyForecast;
import com.weather.dto.openweathermap.Forecast;
import com.weather.dto.openweathermap.OpenWeatherMapResponseDTO;
import com.weather.dto.WeatherResponseDTO;
import com.weather.exception.WeatherServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final OpenWeatherMapClient openWeatherMapClient;
    private final WeatherCache weatherCache;

    @Autowired
    public WeatherService(OpenWeatherMapClient openWeatherMapClient, WeatherCache weatherCache) {
        this.openWeatherMapClient = openWeatherMapClient;
        this.weatherCache = weatherCache;
    }

    public WeatherResponseDTO getForecast(String city, boolean offlineMode) {
        if (offlineMode) {
            return weatherCache.getCachedForecast(city)
                    .orElseThrow(() -> new WeatherServiceException("No cached data available for offline mode"));
        }

        OpenWeatherMapResponseDTO openWeatherMapResponse = openWeatherMapClient.getForecast(city);
        WeatherResponseDTO processedWeatherResponse = processWeatherData(openWeatherMapResponse);
        weatherCache.cacheForecast(city, processedWeatherResponse);
        return processedWeatherResponse;
    }

    private WeatherResponseDTO processWeatherData(OpenWeatherMapResponseDTO openWeatherMapResponse) {
        List<DailyForecast> forecasts = new ArrayList<>();

        // Group forecasts by date
        Map<LocalDate, List<Forecast>> forecastsByDate = openWeatherMapResponse.getForecasts().stream()
                .collect(Collectors.groupingBy(
                        forecast -> LocalDate.parse(forecast.getDtText().split(" ")[0]),
                        TreeMap::new,
                        Collectors.toList()));

        // Process next 3 days
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
                .mapToDouble(f -> f.getMain().getTempMax())
                .max()
                .orElse(0);

        double minTemp = forecasts.stream()
                .mapToDouble(f -> f.getMain().getTempMin())
                .min()
                .orElse(0);

        boolean hasRain = forecasts.stream()
                .anyMatch(f -> f.getWeather().stream()
                        .anyMatch(w -> "Rain".equalsIgnoreCase(w.getMain())));

        boolean hasThunderstorm = forecasts.stream()
                .anyMatch(f -> f.getWeather().stream()
                        .anyMatch(w -> "Thunderstorm".equalsIgnoreCase(w.getMain())));

        double maxWind = forecasts.stream()
                .mapToDouble(f -> f.getWind().getSpeed())
                .max()
                .orElse(0);

        List<String> alerts = new ArrayList<>();
        if (hasRain) alerts.add("Carry umbrella");
        if (maxTemp > 40) alerts.add("Use sunscreen lotion");
        if (maxWind > 10) alerts.add("It's too windy, watch out!");
        if (hasThunderstorm) alerts.add("Don't step out! A Storm is brewing!");

        // Convert temperature from Kelvin to Celsius
        double maxTempCelsius = maxTemp - 273.15;
        double minTempCelsius = minTemp - 273.15;

        return new DailyForecast(date, maxTempCelsius, minTempCelsius, alerts);
    }
}