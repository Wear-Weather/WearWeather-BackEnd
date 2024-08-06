package com.WearWeather.wear.domain.storage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageInfoResponse {

    private Long id;
    private String url;

    public static ImageInfoResponse of(Long id, String url) {
        return ImageInfoResponse.builder()
            .id(id)
            .url(url)
            .build();
    }
}
