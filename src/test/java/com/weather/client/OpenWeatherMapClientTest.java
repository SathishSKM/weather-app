package com.weather.client;

import com.weather.cache.WeatherCache;
import com.weather.dto.WeatherResponseDTO;
import com.weather.dto.openweathermap.City;
import com.weather.dto.openweathermap.OpenWeatherMapResponseDTO;
import com.weather.exception.BadRequestException;
import com.weather.exception.ResourceNotFoundException;
import com.weather.exception.WeatherApiException;
import java.lang.reflect.Field;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("OpenWeatherMapClient")
class OpenWeatherMapClientTest {

  private RestTemplate restTemplate;

  private OpenWeatherMapClient client;

  @Mock
  private WeatherCache weatherCache;


  @BeforeEach
  void setup() throws Exception {
    restTemplate = mock(RestTemplate.class);
    RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
    when(builder.build()).thenReturn(restTemplate);

    client = new OpenWeatherMapClient(builder, weatherCache);

    setPrivateField(client, "apiKey", "dummy-key");
    setPrivateField(client, "apiUrl", "http://dummy-url");
  }

  private void setPrivateField(Object target, String fieldName, String value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  @Nested
  @DisplayName("When API responds successfully")
  class Success {

    @Test
    @DisplayName("Should throw WeatherApiException if body code is not '200'")
    void shouldThrowIfBodyCodeNot200() {
      OpenWeatherMapResponseDTO dto = new OpenWeatherMapResponseDTO();
      dto.setCode("404");

      ResponseEntity<OpenWeatherMapResponseDTO> response = new ResponseEntity<>(dto, HttpStatus.OK);
      when(restTemplate.getForEntity(anyString(), eq(OpenWeatherMapResponseDTO.class))).thenReturn(response);

      WeatherApiException ex = assertThrows(WeatherApiException.class,
          () -> client.getForecast("London"));

      assertTrue(ex.getMessage().contains("API returned error code"));
    }
  }

  @Nested
  @DisplayName("When API responds with client error")
  class ClientErrors {

    @Test
    @DisplayName("Should throw ResourceNotFoundException for 404")
    void shouldThrowNotFound() {
      HttpClientErrorException ex = HttpClientErrorException.create(
          HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null);
      when(restTemplate.getForEntity(anyString(), eq(OpenWeatherMapResponseDTO.class))).thenThrow(ex);

      ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
          () -> client.getForecast("UnknownCity"));

      assertTrue(thrown.getMessage().contains("City not found"));
    }

    @Test
    @DisplayName("Should throw BadRequestException for 400")
    void shouldThrowBadRequest() {
      HttpClientErrorException ex = HttpClientErrorException.create(
          HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, null, null);
      when(restTemplate.getForEntity(anyString(), eq(OpenWeatherMapResponseDTO.class))).thenThrow(ex);

      BadRequestException thrown = assertThrows(BadRequestException.class,
          () -> client.getForecast("InvalidCity"));

      assertTrue(thrown.getMessage().contains("Bad request"));
    }

    @Test
    @DisplayName("Should throw WeatherApiException for other client errors")
    void shouldThrowGenericClientError() {
      HttpClientErrorException ex = HttpClientErrorException.create(
          HttpStatus.FORBIDDEN, "Forbidden", HttpHeaders.EMPTY, null, null);
      when(restTemplate.getForEntity(anyString(), eq(OpenWeatherMapResponseDTO.class))).thenThrow(ex);

      WeatherApiException thrown = assertThrows(WeatherApiException.class,
          () -> client.getForecast("BlockedCity"));

      assertTrue(thrown.getMessage().contains("Client error"));
    }
  }

  @Nested
  @DisplayName("When API call fails due to network or other issues")
  class NetworkErrors {

    @Test
    @DisplayName("Should throw WeatherApiException for RestClientException")
    void shouldThrowRestClientException() {
      when(restTemplate.getForEntity(anyString(), eq(OpenWeatherMapResponseDTO.class)))
          .thenThrow(new RestClientException("Connection timeout"));

      WeatherApiException thrown = assertThrows(WeatherApiException.class,
          () -> client.getForecast("TimeoutCity"));

      assertTrue(thrown.getMessage().contains("Error calling weather API"));
    }

    @Test
    @DisplayName("Should throw WeatherApiException if response body is null")
    void shouldThrowIfBodyIsNull() {
      ResponseEntity<OpenWeatherMapResponseDTO> response = new ResponseEntity<>(null, HttpStatus.OK);
      when(restTemplate.getForEntity(anyString(), eq(OpenWeatherMapResponseDTO.class))).thenReturn(response);

      WeatherApiException thrown = assertThrows(WeatherApiException.class,
          () -> client.getForecast("EmptyCity"));

      assertTrue(thrown.getMessage().contains("Failed to fetch weather data"));
    }
  }
}

