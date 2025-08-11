package com.weather.dto.openweathermap;

import lombok.Data;

import java.io.Serializable;

@Data
public class Weather implements Serializable {
    private int id;

    private String main;

    private String description;

    private String icon;
}
