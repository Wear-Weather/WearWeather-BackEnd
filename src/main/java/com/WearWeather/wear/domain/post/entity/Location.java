package com.WearWeather.wear.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Builder
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(city, location.city) &&
                Objects.equals(district, location.district);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, district);
    }
}
