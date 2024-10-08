package com.WearWeather.wear.domain.location.repository;

import com.WearWeather.wear.domain.location.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<CityIdMapping> findIdByCity(String city);

    Optional<CityMapping> findCityById(Long cityId);
}
