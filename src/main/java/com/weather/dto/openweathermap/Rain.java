package com.weather.dto.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Rain {

    @JsonProperty("3h")
    private Double threeHourVolume;
}
