package com.weather.dto.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Rain implements Serializable {

    @JsonProperty("3h")
    private Double threeHourVolume;
}
