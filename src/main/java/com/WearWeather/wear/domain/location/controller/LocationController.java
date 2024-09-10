package com.WearWeather.wear.domain.location.controller;

import com.WearWeather.wear.domain.location.dto.response.GeocodingLocationResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionsResponse;
import com.WearWeather.wear.domain.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/basic-location")
    public void getLocationData() throws Exception {
        locationService.saveLocationData();
    }

    @PostMapping("/location")
    public Mono<GeocodingLocationResponse> geocodingLocation(@RequestParam("longitude") double longitude,
                                                             @RequestParam("latitude") double latitude){
        return locationService.findLocationByGeoCoordApi(longitude, latitude);
    }

    @GetMapping("/regions")
    public RegionsResponse getRegions(){
        return locationService.getRegions();
    }


}
