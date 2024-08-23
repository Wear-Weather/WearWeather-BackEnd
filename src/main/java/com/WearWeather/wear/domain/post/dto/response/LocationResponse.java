package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

@Builder
public record LocationResponse(
        String city,
        String district
){
    public static LocationResponse of(String city, String district){
        return LocationResponse.builder()
                .city(city)
                .district(district)
                .build();
    }
}
