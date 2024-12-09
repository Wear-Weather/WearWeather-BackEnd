package com.WearWeather.wear.domain.weather.repository;

import com.WearWeather.wear.domain.weather.entity.OutfitGuideImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutfitGuideImageRepository extends JpaRepository<OutfitGuideImage, Long> {

    @Query("SELECT url FROM OutfitGuideImage WHERE guideId = :guideId")
    Optional<List<String>> findGuideImagesByNowTemperature(@Param("guideId") Long guideId);
}
