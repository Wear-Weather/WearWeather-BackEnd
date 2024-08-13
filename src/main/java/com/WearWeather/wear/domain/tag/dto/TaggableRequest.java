package com.WearWeather.wear.domain.tag.dto;

import java.util.Set;

public interface TaggableRequest {

    Set<Long> getWeatherTagIds();

    Set<Long> getTemperatureTagIds();

    Long getSeasonTagId();
}
