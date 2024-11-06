package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

@Builder
public record SearchLocationResponse(
        String address_name,
        String latitude,
        String longitude,
        String city,
        String district
) {
    public static SearchLocationResponse of(String address_name, String latitude, String longitude, String city, String district){
        return SearchLocationResponse.builder()
            .address_name(address_name)
            .latitude(latitude)
            .longitude(longitude)
            .city(city)
            .district(district)
            .build();
    }
}
