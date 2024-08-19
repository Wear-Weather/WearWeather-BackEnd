package com.WearWeather.wear.domain.location.repository;

import com.WearWeather.wear.domain.location.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

}
