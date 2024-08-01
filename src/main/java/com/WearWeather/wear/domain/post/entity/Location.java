package com.WearWeather.wear.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Embeddable
public class Location {

    @Column(nullable = false)
    public String city;

    @Column(nullable = false)
    public String district;

}
