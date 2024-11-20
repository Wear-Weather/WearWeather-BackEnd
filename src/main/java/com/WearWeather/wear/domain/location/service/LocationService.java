package com.WearWeather.wear.domain.location.service;

import com.WearWeather.wear.domain.location.dto.response.*;
import com.WearWeather.wear.domain.location.entity.City;
import com.WearWeather.wear.domain.location.entity.District;
import com.WearWeather.wear.domain.location.repository.CityRepository;
import com.WearWeather.wear.domain.location.repository.DistrictRepository;
import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    @Value("${location.api.accessToken.url}")
    private String locationTokenUrl;
    @Value("${location.api.consumer-key}")
    private String locationServiceId;
    @Value("${location.api.consumer-secret}")
    private String locationServiceSecret;
    @Value("${location.api.base-url}")
    private String locationBaseUrl;

    @Value("${kakao.geo.base-url}")
    private String geoCoordBaseUrl;
    @Value("${kakao.geo.api-key}")
    private String geoCoordApiKey;

    @Value("${kakao.geo.coord.path}")
    private String geoCoordPath;
    @Value("${kakao.geo.search.path}")
    private String geoSearchPath;

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveLocationData(){
        String accessToken = createAccessToken();

        List<CityResponse> cityIdList = cityDateApi(accessToken);
        districtDataApi(cityIdList, accessToken);
    }

    public String createAccessToken(){

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(locationTokenUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(locationTokenUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

         String json = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("consumer_key", locationServiceId)
                        .queryParam("consumer_secret", locationServiceSecret)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode rootNode = objectMapper.readTree(json);

            return rootNode.path("result").path("accessToken").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_CREATE_LOCATION_ACCESS_TOKEN);
        }

    }

    @Transactional
    public List<CityResponse> cityDateApi(String locationAccessToken){

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(locationBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(locationBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("accessToken", locationAccessToken)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::mapCity)
                .block();
    }

    private List<CityResponse> mapCity(String cityResponseBody) {

        List<City> cityList = new ArrayList<>();
        List<CityResponse> cityResponses =  new ArrayList<>();

        City entireCity = City.builder()
                .city("전국")
                .build();

        cityList.add(entireCity);

        try {
            JsonNode root = objectMapper.readTree(cityResponseBody);
            JsonNode documents = root.path("result");

            for (int i =0; i < documents.size(); i++) {
                String fullAddr = documents.get(i).path("full_addr").asText();
                String exchangeCityName = exchangeCityName(fullAddr);

                City city = City.builder()
                        .city(exchangeCityName)
                        .build();

                cityList.add(city);
            }

            List<City> saveCityList = cityRepository.saveAll(cityList);

            Map<String, Long> cityMap = saveCityList.stream()
                    .collect(Collectors.toMap(City::getCity, City::getId));

            for (int i =0; i < documents.size(); i++) {
                String fullAddr = documents.get(i).path("full_addr").asText();
                String exchangeCityName = exchangeCityName(fullAddr);

                int cd = documents.get(i).path("cd").asInt();

                CityResponse locationResponse = CityResponse.builder()
                        .id(cityMap.get(exchangeCityName))
                        .city(exchangeCityName)
                        .apiCityId(cd)
                        .build();

                cityResponses.add(locationResponse);
            }

            return cityResponses;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAIL_SAVE_LOCATION_CITY);
        }
    }

    @Transactional
    private void districtDataApi(List<CityResponse> cityResponses, String locationAccessToken) {

        List<List<District>> districtLists = cityResponses
                .stream()
                .map(cityResponse -> findDistrictByCityId(locationAccessToken, cityResponse))
                .toList();

        List<District> districts = districtLists.stream()
                .flatMap(List::stream).toList();

        districtRepository.saveAll(districts);
    }

    private List<District> findDistrictByCityId(String locationAccessToken, CityResponse response){

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(locationBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(locationBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("accessToken", locationAccessToken)
                        .queryParam("cd", response.apiCityId())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(districtList -> mapDistrict(districtList, response.id()))
                .block();
    }

    private List<District> mapDistrict(String districtResponseBody, Long id){

        List<District> districtList = new ArrayList<>();
        Set<String> uniqueDistrictNames = new HashSet<>();

        try {
            JsonNode root = objectMapper.readTree(districtResponseBody);
            JsonNode documents = root.path("result");

            for (int i = 0; i < documents.size(); i++) {
                String districtName = documents.get(i).path("addr_name").asText();

                if(districtName.contains(" ")){
                    districtName = extractDistrictName(districtName);
                }

                if(uniqueDistrictNames.add(districtName)){
                    District district = District.builder()
                            .cityId(id)
                            .district(districtName)
                            .build();

                    districtList.add(district);
                }
            }

            return districtList;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAIL_SAVE_LOCATION_DISTRICT);
        }
    }

    public String exchangeCityName(String city){

        Set<String> combineFirstAndThirdCharsCityName = new HashSet<>(Arrays.asList("충청남도", "충청북도", "전라남도", "전라북도", "경상남도", "경상북도"));
        Set<String> appendCityToLocationCityName = new HashSet<>(Arrays.asList("세종특별자치시", "제주특별자치도", "강원특별자치도"));

        if (combineFirstAndThirdCharsCityName.contains(city)) {
            return city.charAt(0) + "" + city.charAt(2);
        }

        if(appendCityToLocationCityName.contains(city)){
            return city.substring(0,2) + city.charAt(city.length() - 1);
        }

        return city.substring(0, 2);
    }

    public String extractDistrictName(String districtName){

            int spaceIndex = districtName.indexOf(" ");

            if (spaceIndex == -1) {
                return districtName;
            }

            return districtName.substring(0, spaceIndex);

    }

    public Mono<GeocodingLocationResponse> findLocationByGeoCoordApi(double longitude, double latitude){

        isValidCoordinates(longitude, latitude);
        WebClient webClient = buildKakaoLocalBaseUrl();

        String restApiKey = "KakaoAK " + geoCoordApiKey;

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

            Location location = findCityIdAndDistrictId(exchangedCityName, district);

            return GeocodingLocationResponse.of(exchangedCityName, location.getCity(), district, location.getDistrict());

        } catch (IOException e) {
            throw new CustomException(ErrorCode.GEO_COORD_SERVER_ERROR);
        }
    }

    public static String extractDistrict(String address) {

        int index = Math.min(address.indexOf("시"), address.indexOf("구"));

        if (index == -1) {
            return address;
        }

        return address.substring(0, index + 1);
    }


    public Mono<SearchLocationResponse> searchLocation(String address){

        String restApiKey = "KakaoAK " + geoCoordApiKey;
        WebClient webClient = buildKakaoLocalBaseUrl();

        try {
            String queryParam = URLEncoder.encode(address, "UTF-8");

            return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(geoSearchPath)
                    .queryParam("query", queryParam)
                    .build())
                .header("Authorization", restApiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new CustomException(ErrorCode.GEO_COORD_SERVER_ERROR)));
                })
                .bodyToMono(String.class)
                .map(this::mapSearchLocation);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private SearchLocationResponse mapSearchLocation(String responseBody) {

        String address_name = "address_name";
        String x = "x";
        String y = "y";
        String region_1depth_name = "region_1depth_name";
        String region_2depth_name = "region_2depth_name";

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            if(root.path("documents").isEmpty() || root.path("documents").isNull()){
                return new SearchLocationResponse();
            }

            JsonNode documents = root.path("documents").get(0);
            JsonNode address = documents.path("address");

            String city = address.path(region_1depth_name).asText().substring(0, 2);
            String district = address.path(region_2depth_name).asText();

            if(district.isEmpty() || district.isBlank()){
                return new SearchLocationResponse();
            }

            String address_full_name = extractAddressName(documents.path(address_name).asText());
            String longitude = String.format("%.4f", documents.path(x).asDouble());
            String latitude = String.format("%.4f", documents.path(y).asDouble());

            Location location = findCityIdAndDistrictId(city, district);
            return SearchLocationResponse.of(address_full_name, longitude, latitude, city, location.getCity(), district, location.getDistrict());

        } catch (IOException e) {
            throw new CustomException(ErrorCode.GEO_COORD_SERVER_ERROR);
        }
    }

    private String extractAddressName(String address_name){
        String[] parts = address_name.split(" ", 2);
        String firstPart = parts[0].substring(0, 2);
        String restOfAddress = parts[1];

        return firstPart + " " + restOfAddress;
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
        String cityName = "전국";

        Long cityId = cityRepository.findIdByCity(cityName)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_CITY_ID)).getId();
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


    private WebClient buildKakaoLocalBaseUrl(){

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(geoCoordBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient.builder()
            .uriBuilderFactory(factory)
            .baseUrl(geoCoordBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
