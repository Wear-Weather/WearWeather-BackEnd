package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

@Builder
public record SearchLocationResponse(
        String address_name,
        String longitude,
        String latitude,
        String city,
        Long cityId,
        String district,
        Long districtId
) {

    public static SearchLocationResponse of(String address_name, String longitude, String latitude, String city, Long cityId, String district, Long districtId){
        return SearchLocationResponse.builder()
            .address_name(address_name)
            .longitude(longitude)
            .latitude(latitude)
            .city(city)
            .cityId(cityId)
            .district(district)
            .districtId(districtId)
            .build();
    }
}
