package com.WearWeather.wear.domain.weather.service;

import com.WearWeather.wear.domain.weather.dto.WeatherPerTimeResponse;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    @Value("${weather.api.base-url}")
    private String baseUrl;
    @Value("${weather.api.path-url}")
    private String pathUrl;
    @Value("${weather.api.service-key}")
    private String serviceKey;

    private final ObjectMapper objectMapper;


    public WeatherPerTimeResponse weatherTime(double longitude, double latitude) {

        WebClient webClient = webClient(baseUrl);

        try {

            String encodeServiceKey = URLEncoder.encode(serviceKey, "UTF-8");

            LocalDateTime now = LocalDateTime.now();
            String baseDate = localDate(now);
            String baseTime = getBaseTime(now);

            return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(pathUrl)
                    .queryParam("serviceKey", encodeServiceKey)
                    .queryParam("numOfRows", 10)
                    .queryParam("pageNo", 1)
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    //TODO : 경도위도를 통해 좌표 계산하기
                    .queryParam("nx", 55)
                    .queryParam("ny", 127)
                    .queryParam("dataType", "JSON")
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::mapWeatherTime)
                .block();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private WebClient webClient(String baseUrl) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(EncodingMode.VALUES_ONLY);

        return WebClient.builder()
            .uriBuilderFactory(factory)
            .baseUrl(baseUrl)
            .build();
    }

    private String localDate(LocalDateTime now) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return now.format(formatter);
    }

    private String getBaseTime(LocalDateTime now) {
        int[] baseTimeList = {2, 5, 8, 11, 14, 17, 20, 23};

        int currentHour = now.getHour();
        int minute = now.getMinute();

        int previousBaseTime = -1;
        int selectedBaseTime = -1;

        for (int baseTime : baseTimeList) {
            if (baseTime < currentHour) {
                previousBaseTime = baseTime;
                selectedBaseTime = baseTime;
            } else if (baseTime == currentHour) {
                if (minute <= 20) {
                    selectedBaseTime = previousBaseTime;
                } else {
                    selectedBaseTime = baseTime;
                }
                break;
            } else {
                break;
            }
        }

        return String.format("%02d00", selectedBaseTime);
    }

    private WeatherPerTimeResponse mapWeatherTime(String responseBody) {

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode body = root.path("response").path("body");
            JsonNode items = body.path("items").path("item");

            Map<String, String> weatherValues = new HashMap<>();
            
            for (JsonNode item : items) {
                String category = item.path("category").asText();
                String fcstValue = item.path("fcstValue").asText();

                if (category.equals("TMP") || category.equals("PTY") || category.equals("SKY")) {
                    weatherValues.put(category, fcstValue);
                }
            }

            String tmp = weatherValues.get("TMP");
            String pty = weatherValues.get("PTY");
            String sky = weatherValues.get("SKY");

            String weatherType = null;

            if(pty.equals("0")){
                weatherType = getWeatherTypeBySKY(sky);
            }else {
                weatherType = getWeatherTypeByPTY(pty);
            }

            String message = createWeatherMessage(tmp, pty, sky);
            
            return WeatherPerTimeResponse.of(tmp, weatherType, message);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAIL_WEATHER_API_NO_DATA);
        }
    }

    private String getWeatherTypeByPTY(String pty){

        return switch (pty) {
            case "1" -> "rain";
            case "2" -> "sleet";
            case "3" -> "snow";
            case "4" -> "rain";
            default -> throw new CustomException(ErrorCode.INVALID_PTY_VALUE_WEATHER_API);
        };
    }

    private String getWeatherTypeBySKY(String skyValue){

      return switch (skyValue) {
        case "1" -> "clear";
        case "3" -> "partly_cloudy";
        case "4" -> "cloudy";
        default -> throw new CustomException(ErrorCode.INVALID_SKY_VALUE_WEATHER_API);
      };
    }

    private String createWeatherMessage(String currentTmp, String pty, String sky) {

        String message = createTmpMessage(currentTmp);

        if(message.equals("기본 기온 메시지")){
            message = createPtyMessage(pty);

        }else if(message.equals("쌀쌀하고 ") || message.equals("기본 강수 메시지")){
            message += createSkyMessage(sky);
        }

        return message;
    }

    private String createTmpMessage(String currentTmp){

        String message = "기본 기온 메시지";

        int numTmp = Integer.parseInt(currentTmp);

        if (numTmp <= 0){
            message = "매우 추운 날이에요!";
        }else if (numTmp <= 10){
            message = "쌀쌀하고 ";
        }else if (numTmp > 30){
            message = "매우 더운 날이에요!";
        }

        return message;
    }

    private String createPtyMessage(String pty){

        String message = "기본 강수 메시지";

        switch (pty) {
            case "1" :
                message = "비가 내리는 날이에요!";
                break;
            case "2" :
                message = "눈/비가 내리는 날이에요!";
                break;
            case "3" :
                message = "눈가 내리는 날이에요!";
                break;
            case "4" :
                message = "소나기가 내리는 날이에요!";
                break;
        }

        return message;
    }

    private String createSkyMessage(String sky){

        return switch (sky) {
            case "1" -> "하늘이 맑은 날이에요!";
            case "3" -> "구름이 많은 날이에요!";
            case "4" -> "날씨가 흐린 날이에요!";
            default -> throw new CustomException(ErrorCode.INVALID_SKY_VALUE_WEATHER_API);
        };
    }
}