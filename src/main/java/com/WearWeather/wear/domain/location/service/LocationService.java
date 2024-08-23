package com.WearWeather.wear.domain.location.service;

import com.WearWeather.wear.domain.location.dto.response.DistrictResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionsResponse;
import com.WearWeather.wear.domain.location.entity.City;
import com.WearWeather.wear.domain.location.entity.District;
import com.WearWeather.wear.domain.location.repository.CityRepository;
import com.WearWeather.wear.domain.location.repository.DistrictRepository;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    @Value("${location.api.key}")
    private String openApiKey;

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final RestTemplate restTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void saveLocationData() throws Exception  {

        List<City> cityList = cityRepository.findAll();

        for ( City city : cityList) {
            String apiUrl = "https://api.vworld.kr/req/data?service=data" +
                    "&request=GetFeature" +
                    "&data=LT_C_ADSIGG_INFO" +
                    "&key=" + openApiKey +
                    "&format=json" +
                    "&attrFilter=full_nm:like:" + city.getCity() +
                    "&geometry=false";

            String responseBody = restTemplate.getForEntity(apiUrl, String.class).getBody();

            if (responseBody != null) {
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode features = root.path("response")
                        .path("result")
                        .path("featureCollection")
                        .path("features");

                Set<String> uniqueDistricts = new HashSet<>();

                for (JsonNode feature : features) {
                    JsonNode properties = feature.path("properties");
                    String districtName = properties.path("sig_kor_nm").asText();

                    String processedDistrictName = (districtName.length() > 3)
                            ? districtName.substring(0, 3)
                            : districtName;

                    uniqueDistricts.add(processedDistrictName);
                }

                List<District> districtsToSave = uniqueDistricts.stream()
                        .map(districtName -> new District(city.getId(), districtName))
                        .collect(Collectors.toList());

                districtRepository.saveAll(districtsToSave);
            }
        }
    }

    public RegionsResponse getRegions(){
        List<City> cityList = cityRepository.findAll();
        List<District> districts = districtRepository.findAll();
        Map<Long, List<District>> districtMap = districts.stream()
                .collect(Collectors.groupingBy(District::getCityId));

        List<RegionResponse> regionResponse = getRegionResponseList(cityList, districtMap);

        return RegionsResponse.of(regionResponse);
    }

    private List<RegionResponse> getRegionResponseList(List<City> cityList, Map<Long, List<District>> districtMap){

        List<RegionResponse> regionResponse = new ArrayList<>();
        regionResponse.add(setRegionInitialData());
        regionResponse.addAll(getRegionResponseListByCity(cityList, districtMap));

        return regionResponse;
    }

    private List<RegionResponse> getRegionResponseListByCity(List<City> cityList, Map<Long, List<District>> districtMap){
        return cityList.stream()
                .filter(city -> districtMap.containsKey(city.getId()))
                .map(city -> getRegionResponseByCity(city, districtMap.get(city.getId())))
                .toList();
    }
    private RegionResponse getRegionResponseByCity(City city, List<District> districts){

        List<DistrictResponse> districtByCity = new ArrayList<>();
        districtByCity.add(setDistrictInitialData());

        districtByCity.addAll(getDistrictResponse(districts));

        return RegionResponse.of(city.getId(), city.getCity(), districtByCity);
    }

    private List<DistrictResponse> getDistrictResponse(List<District> districts) {
        return districts.stream()
                .map(district -> DistrictResponse.of(district.getId(), district.getDistrict()))
                .toList();
    }

    private RegionResponse setRegionInitialData(){
        Long cityId = 1L;
        String cityName = "전체";
        List<DistrictResponse> emptyResponse = Collections.emptyList();

        return RegionResponse.of(cityId, cityName, emptyResponse);
    }

    private DistrictResponse setDistrictInitialData(){
        Long districtId = 0L;
        String districtName = "전체";

        return DistrictResponse.of(districtId, districtName);
    }

    public Location findCityIdAndDistrictId(String city, String district){
        Long cityId = findCityIdByCity(city);
        Long districtId = findDistrictIdByCityIdAndDistrict(cityId, district);

        return new Location(cityId, districtId);
    }

    private Long findCityIdByCity(String city){
        return cityRepository.findIdByCity(city)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_CITY_ID)).getId();
    }

    private Long findDistrictIdByCityIdAndDistrict(Long cityId, String district){
        return districtRepository.findIdByCityIdAndDistrict(cityId, district)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_DISTRICT_ID)).getId();
    }
}
