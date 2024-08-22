package com.WearWeather.wear.domain.location.repository;

import com.WearWeather.wear.domain.location.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<DistrictMapping> findDistrictByCityIdAndId(Long cityId, Long districtId);
}
