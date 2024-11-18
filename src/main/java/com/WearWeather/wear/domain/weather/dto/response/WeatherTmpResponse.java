package com.WearWeather.wear.domain.weather.dto.response;

import lombok.Builder;

@Builder
public record WeatherTmpResponse(
        String minTemp,
        String maxTemp
){
  public static WeatherTmpResponse of(String minTemp, String maxTemp){
    return WeatherTmpResponse.builder()
        .minTemp(minTemp)
        .maxTemp(maxTemp)
        .build();
  }
}
