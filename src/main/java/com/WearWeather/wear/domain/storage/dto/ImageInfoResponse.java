package com.WearWeather.wear.domain.storage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageInfoResponse {

    private Long id;
    private String url;
    private int width;
    private int height;

    public static ImageInfoResponse of(Long id, ImageInfoDto imageInfoDto) {
        return ImageInfoResponse.builder()
          .id(id)
          .url(imageInfoDto.getUrl())
          .width(imageInfoDto.getWidth())
          .height(imageInfoDto.getHeight())
          .build();
    }
}

