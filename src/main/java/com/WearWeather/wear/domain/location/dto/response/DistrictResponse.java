package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record DistrictResponse(
        Long districtId,
        String districtName
) {
    public static DistrictResponse of(Long districtId, String districtName){
        return DistrictResponse.builder()
                .districtId(districtId)
                .districtName(districtName)
                .build();
    }
}
