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
        Long cityId,
        String district,
        Long districtId
) {
    public SearchLocationResponse() {
        this(null, null, null, null, null, null, null);
    }

    public static SearchLocationResponse of(String address_name, String latitude, String longitude, String city, Long cityId, String district, Long districtId){
        return SearchLocationResponse.builder()
            .address_name(address_name)
            .latitude(latitude)
            .longitude(longitude)
            .city(city)
            .cityId(cityId)
            .district(district)
            .districtId(districtId)
            .build();
    }
}
