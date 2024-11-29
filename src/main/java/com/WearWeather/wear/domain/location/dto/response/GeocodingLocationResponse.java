package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

@Builder
public record GeocodingLocationResponse(
        String city,
        Long cityId,
        String district,
        Long districtId
) {
    public static GeocodingLocationResponse of(String city, Long cityId, String district, Long districtId){
        return GeocodingLocationResponse.builder()
                .city(city)
                .cityId(cityId)
                .district(district)
                .districtId(districtId)
                .build();
    }
}
