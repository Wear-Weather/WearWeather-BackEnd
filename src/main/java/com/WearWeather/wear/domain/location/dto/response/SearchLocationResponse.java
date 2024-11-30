package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

@Builder
public record SearchLocationResponse(
        String address_name,
        String longitude,
        String latitude,
        String cityName,
        Long cityId,
        String districtName,
        Long districtId
) {

    public static SearchLocationResponse of(String address_name, String longitude, String latitude, String cityName, Long cityId, String districtName, Long districtId){
        return SearchLocationResponse.builder()
            .address_name(address_name)
            .longitude(longitude)
            .latitude(latitude)
            .cityName(cityName)
            .cityId(cityId)
            .districtName(districtName)
            .districtId(districtId)
            .build();
    }
}
