package com.WearWeather.wear.domain.location.controller;

import com.WearWeather.wear.domain.location.dto.response.GeocodingLocationResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionsResponse;
import com.WearWeather.wear.domain.location.dto.response.SearchLocationResponse;
import com.WearWeather.wear.domain.location.service.LocationService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/location")
    public ResponseEntity<GeocodingLocationResponse> geocodingLocation(@RequestParam("longitude") double longitude,
      @RequestParam("latitude") double latitude) {
        GeocodingLocationResponse response = locationService.findLocationByGeoCoordApi(longitude, latitude).block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/location/search")
    public List<SearchLocationResponse> searchLocation(@RequestParam(required = true, value= "address") String address){
        if(address.isEmpty()){
            return Collections.emptyList();
        }

        return locationService.searchLocation(address);
    }

    @GetMapping("/regions")
    public RegionsResponse getRegions(){
        return locationService.getRegions();
    }


}
