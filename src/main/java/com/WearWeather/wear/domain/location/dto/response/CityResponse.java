package com.WearWeather.wear.domain.location.dto.response;

import lombok.Builder;

@Builder
public record CityResponse (
        Long id,
        String city,
        int apiCityId
){
}
