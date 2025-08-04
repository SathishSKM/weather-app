package com.weather.dto.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherMapResponseDTO {

    @JsonProperty("cod")
    private String code;

    private int message;

    private int cnt;

    @JsonProperty("list")
    private List<Forecast> forecasts;

    private City city;
}
