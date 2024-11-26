package com.WearWeather.wear.domain.location.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SearchLocationResponse(
        String address_name,
        String longitude,
        String latitude,
        String city,
        String district
) {
    public static SearchLocationResponse of(String address_name, String longitude, String latitude, String city, String district){
        return SearchLocationResponse.builder()
            .address_name(address_name)
            .longitude(longitude)
            .latitude(latitude)
            .city(city)
            .district(district)
            .build();
    }
}
