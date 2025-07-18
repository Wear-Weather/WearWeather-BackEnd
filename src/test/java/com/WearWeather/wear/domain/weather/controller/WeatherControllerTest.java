package com.WearWeather.wear.domain.weather.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.weather.dto.response.OutfitGuideResponse;
import com.WearWeather.wear.domain.weather.dto.response.WeatherPerTimeResponse;
import com.WearWeather.wear.domain.weather.dto.response.WeatherTmpResponse;
import com.WearWeather.wear.domain.weather.service.WeatherService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(WeatherController.class)
@AutoConfigureMockMvc(addFilters = false)
class WeatherControllerTest extends RestDocsTestSupport {

    @MockBean
    private WeatherService weatherService;

    @Test
    @DisplayName("시간대별 날씨 조회 API")
    void get_weather_per_time() throws Exception {
        // given
        double longitude = 127.1234;
        double latitude = 37.1234;

        given(weatherService.weatherTime(longitude, latitude))
          .willReturn(WeatherPerTimeResponse.of("24.0", "맑음", "선선한 날씨입니다."));

        // when & then
        mockMvc.perform(get("/weather/time")
            .param("longitude", String.valueOf(longitude))
            .param("latitude", String.valueOf(latitude)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.currentTemp").value("24.0"))
          .andExpect(jsonPath("$.weatherType").value("맑음"))
          .andExpect(jsonPath("$.weatherMessage").value("선선한 날씨입니다."))
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("longitude").description("경도"),
              parameterWithName("latitude").description("위도")
            ),
            responseFields(
              fieldWithPath("currentTemp").description("현재 기온"),
              fieldWithPath("weatherType").description("날씨 유형"),
              fieldWithPath("weatherMessage").description("날씨 메시지")
            )
          ));
    }

    @Test
    @DisplayName("최저/최고 기온 조회 API")
    void get_weather_tmp() throws Exception {
        // given
        double longitude = 127.1234;
        double latitude = 37.1234;

        given(weatherService.weatherTmp(longitude, latitude))
          .willReturn(WeatherTmpResponse.of("18.0", "27.0"));

        // when & then
        mockMvc.perform(get("/weather/tmp")
            .param("longitude", String.valueOf(longitude))
            .param("latitude", String.valueOf(latitude)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.minTemp").value("18.0"))
          .andExpect(jsonPath("$.maxTemp").value("27.0"))
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("longitude").description("경도"),
              parameterWithName("latitude").description("위도")
            ),
            responseFields(
              fieldWithPath("minTemp").description("최저 기온"),
              fieldWithPath("maxTemp").description("최고 기온")
            )
          ));
    }

    @Test
    @DisplayName("기온 기반 옷차림 가이드 조회 API")
    void get_outfit_guide() throws Exception {
        // given
        int tmp = 23;

        given(weatherService.getOutfitGuide(tmp))
          .willReturn(OutfitGuideResponse.of(
            "간절기",
            "가벼운 겉옷이 필요해요.",
            "얇은 니트와 청바지",
            List.of("image1.jpg", "image2.jpg")
          ));

        // when & then
        mockMvc.perform(get("/weather/guide/outfit")
            .param("tmp", String.valueOf(tmp)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.category").value("간절기"))
          .andExpect(jsonPath("$.categorySentence").value("가벼운 겉옷이 필요해요."))
          .andExpect(jsonPath("$.outfit").value("얇은 니트와 청바지"))
          .andExpect(jsonPath("$.outfitImages").isArray())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("tmp").description("현재 기온")
            ),
            responseFields(
              fieldWithPath("category").description("카테고리"),
              fieldWithPath("categorySentence").description("추천 문구"),
              fieldWithPath("outfit").description("추천 옷차림"),
              fieldWithPath("outfitImages").description("추천 옷차림 이미지 리스트")
            )
          ));
    }
}
