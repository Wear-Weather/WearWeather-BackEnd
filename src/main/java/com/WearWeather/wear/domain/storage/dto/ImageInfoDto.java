package com.WearWeather.wear.domain.storage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageInfoDto {

    private String s3Name;
    private String url;

    public static ImageInfoDto of(String s3Name, String url) {
        return ImageInfoDto.builder()
          .s3Name(s3Name)
          .url(url)
          .build();
    }
}
