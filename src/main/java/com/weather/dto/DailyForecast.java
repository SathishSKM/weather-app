package com.weather.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class DailyForecast implements Serializable {

    private LocalDate date;

    private double maxTemp;

    private double minTemp;

    private List<String> alerts;

    public DailyForecast(LocalDate date, double maxTemp, double minTemp, List<String> alerts) {
        this.date = date;
        this.maxTemp = Math.round(maxTemp * 10) / 10.0;
        this.minTemp = Math.round(minTemp * 10) / 10.0;
        this.alerts = alerts;
    }
}
