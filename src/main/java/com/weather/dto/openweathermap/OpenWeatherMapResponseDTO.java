package com.weather.dto.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OpenWeatherMapResponseDTO implements Serializable {

    @JsonProperty("cod")
    private String code;

    private int message;

    private int cnt;

    @JsonProperty("list")
    private List<Forecast> forecasts;

    private City city;
}
