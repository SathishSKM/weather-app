package com.weather.service;

import com.weather.cache.WeatherCache;
import com.weather.client.OpenWeatherMapClient;
import com.weather.dto.DailyForecast;
import com.weather.dto.WeatherResponseDTO;
import com.weather.dto.openweathermap.*;
import com.weather.exception.WeatherServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("WeatherService BDD Tests")
class WeatherServiceTest {

  @Mock
  private OpenWeatherMapClient openWeatherMapClient;

  @Mock
  private WeatherCache weatherCache;

  @InjectMocks
  private WeatherService weatherService;

  public WeatherServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("When offline mode is enabled")
  class OfflineMode {

    @Test
    @DisplayName("Should return cached forecast if available")
    void shouldReturnCachedForecast() {
      WeatherResponseDTO cached = new WeatherResponseDTO("Paris", Collections.emptyList());
      when(weatherCache.getCachedForecast("Paris")).thenReturn(cached);

      WeatherResponseDTO result = weatherService.getForecast("Paris", true);

      assertEquals("Paris", result.getCity());
      verify(weatherCache).getCachedForecast("Paris");
    }

    @Test
    @DisplayName("Should throw exception if no cached data")
    void shouldThrowIfNoCachedData() {
      when(weatherCache.getCachedForecast("Paris")).thenReturn(null);

      WeatherServiceException ex = assertThrows(WeatherServiceException.class,
          () -> weatherService.getForecast("Paris", true));

      assertEquals("No cached data available for offline mode", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("When online mode is enabled")
  class OnlineMode {

    @Test
    @DisplayName("Should fetch and process forecast from API and cache it")
    void shouldFetchAndCacheForecast() {
      Forecast forecast = new Forecast();
      forecast.setDtText(LocalDate.now() + " 12:00:00");
      Main main = new Main();
      main.setTempMax(300);
      main.setTempMin(280);
      forecast.setMain(main);
      Weather weather = new Weather();
      weather.setMain("Rain");
      forecast.setWeather(List.of(weather));
      Wind wind = new Wind();
      wind.setSpeed(12);
      forecast.setWind(wind);

      OpenWeatherMapResponseDTO response = new OpenWeatherMapResponseDTO();
      City city = new City();
      city.setName("Paris");
      response.setCity(city);
      response.setForecasts(List.of(forecast));

      when(openWeatherMapClient.getForecast("Paris")).thenReturn(response);

      WeatherResponseDTO result = weatherService.getForecast("Paris", false);

      assertEquals("Paris", result.getCity());
      assertFalse(result.getForecasts().isEmpty());
      assertTrue(result.getForecasts().get(0).getAlerts().contains("Carry umbrella"));
      verify(weatherCache).cacheForecast(eq("Paris"), any());
    }

    @Test
    @DisplayName("Should return cached forecast if API fails")
    void shouldReturnCachedIfApiFails() {
      when(openWeatherMapClient.getForecast("Paris")).thenThrow(new RuntimeException("API down"));
      WeatherResponseDTO cached = new WeatherResponseDTO("Paris", Collections.emptyList());
      when(weatherCache.getCachedForecast("Paris")).thenReturn(cached);

      WeatherResponseDTO result = weatherService.getForecast("Paris", false);

      assertEquals("Paris", result.getCity());
    }

    @Test
    @DisplayName("Should rethrow exception if API fails and no cached data")
    void shouldRethrowIfNoCachedData() {
      when(openWeatherMapClient.getForecast("Paris")).thenThrow(new RuntimeException("API down"));
      when(weatherCache.getCachedForecast("Paris")).thenReturn(null);

      RuntimeException ex = assertThrows(RuntimeException.class,
          () -> weatherService.getForecast("Paris", false));

      assertEquals("API down", ex.getMessage());
    }
  }
}
