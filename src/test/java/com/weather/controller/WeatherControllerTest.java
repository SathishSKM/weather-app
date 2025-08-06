package com.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.dto.DailyForecast;
import com.weather.dto.WeatherResponseDTO;
import com.weather.exception.ResourceNotFoundException;
import com.weather.service.WeatherService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WeatherController")
class WeatherControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private WeatherService weatherService;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  @DisplayName("GET /api/weather/forecast")
  class GetForecast {

    @Test
    @DisplayName("Should return 200 and forecast for valid city")
    void shouldReturnForecast() throws Exception {
      WeatherResponseDTO response = new WeatherResponseDTO("Chennai", List.of(
          new DailyForecast(LocalDate.now(), 30.0, 25.0, List.of("Stay hydrated") )
      ));

      Mockito.when(weatherService.getForecast(eq("Chennai"), eq(false))).thenReturn(response);

      mockMvc.perform(get("/api/weather/forecast")
              .param("city", "Chennai")
              .param("offlineMode", "false"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.city").value("Chennai"))
          .andExpect(jsonPath("$.forecasts[0].alerts[0]").value("Stay hydrated"));
    }

    @Test
    @DisplayName("Should return 200 for offline mode")
    void shouldReturnOfflineForecast() throws Exception {
      WeatherResponseDTO response = new WeatherResponseDTO("Delhi", List.of());

      Mockito.when(weatherService.getForecast(eq("Delhi"), eq(true))).thenReturn(response);

      mockMvc.perform(get("/api/weather/forecast")
              .param("city", "Delhi")
              .param("offlineMode", "true"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.city").value("Delhi"));
    }

    @Test
    @DisplayName("Should return 400 if city is missing")
    void shouldReturnBadRequestForMissingCity() throws Exception {
      mockMvc.perform(get("/api/weather/forecast"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 if city not found")
    void shouldReturnNotFound() throws Exception {
      Mockito.when(weatherService.getForecast(eq("Atlantis"), eq(false)))
          .thenThrow(new ResourceNotFoundException("City not found"));

      mockMvc.perform(get("/api/weather/forecast")
              .param("city", "Atlantis"))
          .andExpect(status().isNotFound());
    }
  }
}
