package com.weather.dto;
import lombok.Data;

import java.util.List;

@Data
public class WeatherResponseDTO {
    private String city;
    private List<DailyForecast> forecasts;

    public WeatherResponseDTO(String city, List<DailyForecast> forecasts) {
        this.city = city;
        this.forecasts = forecasts;
    }
}