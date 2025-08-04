package com.weather.client;

import com.weather.dto.openweathermap.OpenWeatherMapResponseDTO;
import com.weather.exception.WeatherApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class OpenWeatherMapClient {

    @Value("${open.weather.api.key}")
    private String apiKey;

    @Value("${open.weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public OpenWeatherMapClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public OpenWeatherMapResponseDTO getForecast(String city) {
        String url = String.format("%s?q=%s&appid=%s&cnt=20", apiUrl, city, apiKey);

        try {
            log.info(url);
            ResponseEntity<OpenWeatherMapResponseDTO> response = restTemplate.getForEntity(
                    url, OpenWeatherMapResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                if (!"200".equals(response.getBody().getCode())) {
                    throw new WeatherApiException("API returned error code: " + response.getBody().getCode());
                }
                return response.getBody();
            }
            throw new WeatherApiException("Failed to fetch weather data");
        } catch (RestClientException e) {
            throw new WeatherApiException("Error calling weather API: " + e.getMessage());
        }
    }
}