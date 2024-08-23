package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

import java.util.List;

@Builder

public record RegionsResponse (
        List<RegionResponse> region
){
    public static RegionsResponse of(List<RegionResponse> region){
        return RegionsResponse.builder()
                .region(region)
                .build();
    }
}
