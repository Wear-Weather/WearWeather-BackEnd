package com.WearWeather.wear.domain.weather.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

            return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(pathUrl)
                    .queryParam("serviceKey", encodeServiceKey)
                    .queryParam("numOfRows", 10)
                    .queryParam("pageNo", 1)
                    //TODO : 현재 날짜 받아오기
                    .queryParam("base_date", 20241113)
                    //TODO : 현재 시간으로 예보시간 받아오기
                    .queryParam("base_time", "0500")
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
}