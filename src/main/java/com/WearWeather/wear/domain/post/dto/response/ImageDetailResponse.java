package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

@Builder
public record ImageDetailResponse(
        Long imageId,
        String url
) {
    public static ImageDetailResponse of(Long imageId, String url){
        return ImageDetailResponse.builder()
                .imageId(imageId)
                .url(url)
                .build();
    }
}
