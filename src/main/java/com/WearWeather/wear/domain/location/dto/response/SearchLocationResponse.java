package com.WearWeather.wear.domain.location.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SearchLocationResponse(
        String address_name,
        String latitude,
        String longitude,
        String city,
        String district
) {
    public SearchLocationResponse() {
        this(null, null, null, null, null);
    }

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
