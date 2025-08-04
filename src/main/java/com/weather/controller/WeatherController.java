package com.weather.controller;

import com.weather.dto.WeatherResponseDTO;
import com.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather API", description = "Weather forecast operations")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    @Operation(summary = "Get 3-day weather forecast for a city")
    public ResponseEntity<WeatherResponseDTO> getForecast(
            @RequestParam String city,
            @RequestParam(required = false, defaultValue = "false") boolean offlineMode) {
        WeatherResponseDTO weatherResponse = weatherService.getForecast(city, offlineMode);
        return ResponseEntity.ok(weatherResponse);
    }
}
