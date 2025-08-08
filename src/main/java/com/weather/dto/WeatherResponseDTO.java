package com.weather.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WeatherResponseDTO implements Serializable {
    private String city;
    private List<DailyForecast> forecasts;

    public WeatherResponseDTO(String city, List<DailyForecast> forecasts) {
        this.city = city;
        this.forecasts = forecasts;
    }
}