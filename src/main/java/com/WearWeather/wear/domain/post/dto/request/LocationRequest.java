package com.WearWeather.wear.domain.post.dto.request;

import com.WearWeather.wear.domain.post.entity.Location;

public record LocationRequest (
        Long city,
        Long district
) {

    public Location toEntity(){
        return Location.builder()
                .city(city)
                .district(district)
                .build();
    }
}