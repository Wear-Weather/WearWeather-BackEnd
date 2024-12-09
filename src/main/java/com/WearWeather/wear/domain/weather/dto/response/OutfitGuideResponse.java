package com.WearWeather.wear.domain.weather.dto.response;

import lombok.Builder;

@Builder
public record OutfitGuideResponse(
      String category,
      String categorySentence,
      String outfit
){
  public static OutfitGuideResponse of(String category, String categorySentence, String outfit){
    return OutfitGuideResponse.builder()
        .category(category)
        .categorySentence(categorySentence)
        .outfit(outfit)
        .build();
  }
}
