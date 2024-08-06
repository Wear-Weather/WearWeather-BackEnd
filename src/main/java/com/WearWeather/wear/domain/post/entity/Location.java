package com.WearWeather.wear.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Location {

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    public Location() {
    }

    public Location(String city, String district) {
        this.city = city;
        this.district = district;
    }
}
