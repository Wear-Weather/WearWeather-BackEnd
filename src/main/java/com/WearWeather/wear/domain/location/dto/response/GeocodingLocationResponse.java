package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

@Builder
public record GeocodingLocationResponse(
        String city,
        String district
) {
    public static GeocodingLocationResponse of(String city, String district){
        return GeocodingLocationResponse.builder()
                .city(city)
                .district(district)
                .build();
    }
}
