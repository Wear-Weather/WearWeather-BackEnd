package com.WearWeather.wear.domain.post.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Location {

    private String city;
    private String district;
    
    public Location() {
    }

    public Location(String city, String district) {
        this.city = city;
        this.district = district;
    }
}
