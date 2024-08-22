package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ImagesResponse(
        List<ImageDetailResponse> image
) {
    public static ImagesResponse of(List<ImageDetailResponse> image){
        return ImagesResponse.builder()
                .image(image)
                .build();
    }
}
