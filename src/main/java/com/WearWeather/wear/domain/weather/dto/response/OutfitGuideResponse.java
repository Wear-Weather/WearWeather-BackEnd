package com.WearWeather.wear.domain.weather.dto.response;

import lombok.Builder;

@Builder
public record OutfitGuideResponse(
        String categorySentence,
        String outfit
){
  public static OutfitGuideResponse of(String categorySentence, String outfit){
    return OutfitGuideResponse.builder()
        .categorySentence(categorySentence)
        .outfit(outfit)
        .build();
  }
}
