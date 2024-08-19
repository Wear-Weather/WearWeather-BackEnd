package com.WearWeather.wear.domain.location.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class District {

    @Id
    @Column(name = "district_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cityId;

    @Column(nullable = false)
    private String district;

    @Builder
    public District(Long cityId, String district) {
        this.cityId = cityId;
        this.district = district;
    }
}
