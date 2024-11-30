package com.WearWeather.wear.domain.weather.controller;

import com.WearWeather.wear.domain.weather.dto.response.WeatherPerTimeResponse;
import com.WearWeather.wear.domain.weather.dto.response.WeatherTmpResponse;
import com.WearWeather.wear.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/time")
    public WeatherPerTimeResponse getWeatherPerTime(@RequestParam("longitude") double longitude,
                                     @RequestParam("latitude") double latitude){
        return weatherService.weatherTime(longitude, latitude);
    }

    @GetMapping("/tmp")
    public WeatherTmpResponse getWeatherTmp(@RequestParam("longitude") double longitude,
                                            @RequestParam("latitude") double latitude){
        return weatherService.weatherTmp(longitude, latitude);
    }

}
