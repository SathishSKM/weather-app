package com.weather.dto.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Forecast implements Serializable {
    private long dt;

    private Main main;

    private List<Weather> weather;

    private Clouds clouds;

    private Wind wind;

    private Rain rain;

    private Sys sys;

    @JsonProperty("dt_txt")
    private String dtText;
}
