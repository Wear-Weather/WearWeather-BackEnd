package com.WearWeather.wear.domain.weather.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record OutfitGuideResponse(
      String category,
      String categorySentence,
      String outfit,
      List<String> outfitImages
){
  public static OutfitGuideResponse of(String category, String categorySentence, String outfit, List<String> outfitImages){
    return OutfitGuideResponse.builder()
        .category(category)
        .categorySentence(categorySentence)
        .outfit(outfit)
        .outfitImages(outfitImages)
        .build();
  }
}
