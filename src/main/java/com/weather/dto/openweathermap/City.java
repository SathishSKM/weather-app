package com.weather.dto.openweathermap;

import lombok.Data;

@Data
public class City {

    private long id;
    private String name;
    private Coord coord;
    private String country;
    private long population;
    private int timezone;
    private long sunrise;
    private long sunset;
}
