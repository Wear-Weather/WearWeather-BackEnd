package com.WearWeather.wear.domain.location.service;

import com.WearWeather.wear.domain.location.dto.response.DistrictResponse;
import com.WearWeather.wear.domain.location.dto.response.GeocodingLocationResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionsResponse;
import com.WearWeather.wear.domain.location.entity.City;
import com.WearWeather.wear.domain.location.entity.District;
import com.WearWeather.wear.domain.location.repository.CityRepository;
import com.WearWeather.wear.domain.location.repository.DistrictRepository;
import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.domain.post.entity.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    @Value("${location.api.key}")
    private String openApiKey;

    @Value("${kakao.geo.coord.base-url}")
    private String geoCoordBaseUrl;
    @Value("${kakao.geo.coord.path}")
    private String geoCoordPath;
    @Value("${kakao.geo.coord.api-key}")
    private String geoCoordApiKey;

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public void saveLocationData() throws Exception  {
        ObjectMapper objectMapper = new ObjectMapper();

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

    public Mono<GeocodingLocationResponse> findLocationByGeoCoordApi(double longitude, double latitude){

        isValidCoordinates(longitude, latitude);

        String restApiKey = "KakaoAK " + geoCoordApiKey;

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(geoCoordBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(geoCoordBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(geoCoordPath)
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .build())
                .header("Authorization", restApiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                     return clientResponse.bodyToMono(String.class)
                             .flatMap(body -> Mono.error(new CustomException(ErrorCode.INVALID_REQUEST_PARAMETER)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                     return clientResponse.bodyToMono(String.class)
                             .flatMap(body -> Mono.error(new CustomException(ErrorCode.GEO_COORD_SERVER_ERROR)));
                })
                .bodyToMono(String.class)
                 .map(this::mapLocation);
    }

    public void isValidCoordinates(double longitude, double latitude){
        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }
    }

    private GeocodingLocationResponse mapLocation(String responseBody) {

        String region_1depth_city = "region_1depth_name";
        String region_2depth_district = "region_2depth_name";

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode documents = root.path("documents").get(0);

            String city = documents.path(region_1depth_city).asText();
            String exchangedCityName = exchangeCityName(city);
            String district = extractDistrict(documents.path(region_2depth_district).asText());

            return GeocodingLocationResponse.of(exchangedCityName, district);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public String exchangeCityName(String city){

        Set<String> matchCityName = new HashSet<>(Arrays.asList("충청북도", "충청남도", "전라남도", "경상북도", "경상남도", "전북특별자치도"));

        if (!matchCityName.contains(city)) {
            return city.substring(0, 2) + city.charAt(city.length() - 1);
        }

        if(city.equals("전북특별자치도")){
            return city.substring(0, 2);
        }
        return city;
    }

    public static String extractDistrict(String address) {

        int index = Math.min(address.indexOf("시"), address.indexOf("구"));

        if (index == -1) {
            return address;
        }

        return address.substring(0, index + 1);
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

    public LocationResponse findCityIdAndDistrictId(Long cityId, Long districtId){
        String city = findCityById(cityId);
        String district = findDistrictByCityIdAndDistrictId(cityId, districtId);

        return LocationResponse.of(city, district);
    }

    private String findCityById(Long cityId){
        return cityRepository.findCityById(cityId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_CITY)).getCity();
    }

    private String findDistrictByCityIdAndDistrictId(Long cityId, Long districtId){
        return districtRepository.findDistrictByCityIdAndId(cityId, districtId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_DISTRICT)).getDistrict();
    }
}
