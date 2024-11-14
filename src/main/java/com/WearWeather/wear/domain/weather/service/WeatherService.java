package com.WearWeather.wear.domain.weather.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


    public String weatherTime(double longitude, double latitude) {

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
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private WebClient webClient(String baseUrl){

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(EncodingMode.VALUES_ONLY);

        return WebClient.builder()
            .uriBuilderFactory(factory)
            .baseUrl(baseUrl)
            .build();
    }

    private String localDate(LocalDateTime now){

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
}