package com.WearWeather.wear.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Location {

    @Column(nullable = false)
    private Long city;

    @Column(nullable = false)
    private Long district;

    public Location() {
    }

    public Location(Long city, Long district) {
        this.city = city;
        this.district = district;
    }
}
