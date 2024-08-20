package com.WearWeather.wear.domain.location.controller;

import com.WearWeather.wear.domain.location.dto.response.RegionsResponse;
import com.WearWeather.wear.domain.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/location")
    public void getLocationDistrictData() throws Exception {
        locationService.getLocationData();
    }

    @GetMapping("/regions")
    public RegionsResponse getRegions(){
        return locationService.getRegions();
    }


}
