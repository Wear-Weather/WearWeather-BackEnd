package com.WearWeather.wear.domain.weather.dto;

import lombok.Builder;

@Builder
public record WeatherPerTimeResponse(
        String currentTemp,
        String weatherType,
        String weatherMessage
){
  public static WeatherPerTimeResponse of(String tmp, String weatherType, String weatherMessage){
    return WeatherPerTimeResponse.builder()
        .currentTemp(tmp)
        .weatherType(weatherType)
        .weatherMessage(weatherMessage)
        .build();
  }
}
