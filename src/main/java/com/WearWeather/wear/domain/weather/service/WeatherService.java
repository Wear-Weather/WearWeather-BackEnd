package com.WearWeather.wear.domain.weather.service;

import com.WearWeather.wear.domain.weather.domain.LatXLngY;
import com.WearWeather.wear.domain.weather.dto.response.WeatherPerTimeResponse;
import com.WearWeather.wear.domain.weather.dto.response.WeatherTmpResponse;
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

    public String weatherApi(double longitude, double latitude, int numOfRows) {

        WebClient webClient = webClient(baseUrl);

        try {

            String encodeServiceKey = URLEncoder.encode(serviceKey, "UTF-8");

            LocalDateTime now = LocalDateTime.now();
            String baseDate = localDate(now);
            String baseTime = getBaseTime(now);

            LatXLngY latXLngY = convertGRID_GPS("toXY", latitude, longitude);
            return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(pathUrl)
                    .queryParam("serviceKey", encodeServiceKey)
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("pageNo", 1)
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    .queryParam("nx", String.valueOf((int) latXLngY.x))
                    .queryParam("ny", String.valueOf((int) latXLngY.y))
                    .queryParam("dataType", "JSON")
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
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

    public WeatherPerTimeResponse weatherTime(double longitude, double latitude){
        int numOfRows = 10;
        String responseBody = weatherApi(longitude, latitude, numOfRows);
        return mapWeatherTime(responseBody);
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

    public WeatherTmpResponse weatherTmp(double longitude, double latitude) {
        int numOfRows = 500;
        String responseBody = weatherApi(longitude, latitude, numOfRows);
        return mapWeatherTmp(responseBody);
    }

    private WeatherTmpResponse mapWeatherTmp(String responseBody) {

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode body = root.path("response").path("body");
            JsonNode items = body.path("items").path("item");

            String minTemp = null;
            String maxTemp = null;

            for (JsonNode item : items) {
                String category = item.path("category").asText();
                String fcstValue = item.path("fcstValue").asText();

                if (category.equals("TMN")){
                    minTemp = fcstValue;
                }

                if(category.equals("TMX")) {
                    maxTemp = fcstValue;
                }
            }

            if (minTemp == null || maxTemp == null) {
                throw new CustomException(ErrorCode.INVALID_WEATHER_TMP);
            }

            return WeatherTmpResponse.of(minTemp, maxTemp);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAIL_WEATHER_API_NO_DATA);
        }
    }

    private LatXLngY convertGRID_GPS(String code, double lat_X, double lng_Y) {

        double RE = 6371.00877;  // 지구 반경(km)
        double GRID = 5.0;      // 격자 간격(km)
        double SLAT1 = 30.0;    // 투영 위도1(degree)
        double SLAT2 = 60.0;    // 투영 위도2(degree)
        double OLON = 126.0;    // 기준점 경도(degree)
        double OLAT = 38.0;     // 기준점 위도(degree)
        double XO = 43;             // 기준점 X좌표(GRID)
        double YO = 136;            // 기준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = (Math.pow(sf, sn) * Math.cos(slat1)) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = (re * sf) / Math.pow(ro, sn);

        LatXLngY rs = new LatXLngY();
        if ("toXY".equals(code)) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = (re * sf) / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        } else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) ra = -ra;
            double alat = Math.pow((re * sf) / ra, 1.0 / sn);
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) theta = -theta;
                } else {
                    theta = Math.atan2(xn, yn);
                }
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }

        return rs;
        }

}