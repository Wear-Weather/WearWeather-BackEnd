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
    private long byte_size;

    public static ImageInfoResponse of(Long id, ImageInfoDto imageInfoDto, long byte_size) {
        return ImageInfoResponse.builder()
          .id(id)
          .url(imageInfoDto.getUrl())
          .width(imageInfoDto.getWidth())
          .height(imageInfoDto.getHeight())
          .byte_size(byte_size)
          .build();
    }
}

