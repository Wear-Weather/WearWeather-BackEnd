package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Gender;

public record PostWithLocationName(
        Long postId,
        Long thumbnailImageId,
        String cityName,
        String districtName,
        Gender gender
) {
}