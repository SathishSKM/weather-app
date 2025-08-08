package com.weather.dto.openweathermap;

import lombok.Data;

import java.io.Serializable;

@Data
public class Coord implements Serializable {

    private double lat;
    private double lon;
}
