package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

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
