package com.WearWeather.wear.domain.location.controller;

import com.WearWeather.wear.domain.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/location")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public void getLocationDistrictData() throws Exception {
        locationService.getLocationData();
    }

}
