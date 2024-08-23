package com.WearWeather.wear.domain.post.dto.response;

public record PostWithLocationName(
        Long postId,
        Long thumbnailImageId,
        String cityName,
        String districtName
) {
}