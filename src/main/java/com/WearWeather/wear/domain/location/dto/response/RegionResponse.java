package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RegionResponse(
        Long cityId,
        String cityName,
        List<DistrictResponse> district
){
    public static RegionResponse of(Long cityId, String cityName, List<DistrictResponse> district){
        return RegionResponse.builder()
                .cityId(cityId)
                .cityName(cityName)
                .district(district)
                .build();
    }
}
