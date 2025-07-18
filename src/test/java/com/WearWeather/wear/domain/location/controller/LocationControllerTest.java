package com.WearWeather.wear.domain.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.location.dto.response.DistrictResponse;
import com.WearWeather.wear.domain.location.dto.response.GeocodingLocationResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionResponse;
import com.WearWeather.wear.domain.location.dto.response.RegionsResponse;
import com.WearWeather.wear.domain.location.dto.response.SearchLocationResponse;
import com.WearWeather.wear.domain.location.service.LocationService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import reactor.core.publisher.Mono;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
class LocationControllerTest extends RestDocsTestSupport {

    @MockBean
    private LocationService locationService;

    @Test
    @DisplayName("기본 위치 데이터 저장 API")
    void getLocationData() throws Exception {
        mockMvc.perform(get("/basic-location"))
          .andExpect(status().isOk())
          .andDo(print())
          .andDo(restDocs.document());
    }

    @Test
    @DisplayName("좌표 기반 위치 정보 조회 API")
    void geocodingLocation() throws Exception {
        GeocodingLocationResponse response = GeocodingLocationResponse.of("서울", 1L, "강남구", 2L);

        given(locationService.findLocationByGeoCoordApi(127.0, 37.5))
          .willReturn(Mono.just(response));

        mockMvc.perform(get("/location")
            .param("longitude", "127.0")
            .param("latitude", "37.5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.city").value("서울"))
          .andExpect(jsonPath("$.district").value("강남구"))
          .andDo(print())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("longitude").description("경도"),
              parameterWithName("latitude").description("위도")
            ),
            responseFields(
              fieldWithPath("city").description("도시 이름"),
              fieldWithPath("cityId").description("도시 ID"),
              fieldWithPath("district").description("구 이름"),
              fieldWithPath("districtId").description("구 ID")
            )
          ));
    }

    @Test
    @DisplayName("주소 기반 위치 검색 API")
    void searchLocation() throws Exception {
        List<SearchLocationResponse> results = List.of(
          SearchLocationResponse.of("서울 강남구", "127.0", "37.5", "서울", 1L, "강남구", 2L)
        );

        given(locationService.searchLocation("서울"))
          .willReturn(results);

        mockMvc.perform(get("/location/search")
            .param("address", "서울"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].address_name").value("서울 강남구"))
          .andDo(print())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("address").description("검색할 주소 문자열")
            ),
            responseFields(
              fieldWithPath("[].address_name").description("전체 주소"),
              fieldWithPath("[].longitude").description("경도"),
              fieldWithPath("[].latitude").description("위도"),
              fieldWithPath("[].cityName").description("도시 이름"),
              fieldWithPath("[].cityId").description("도시 ID"),
              fieldWithPath("[].districtName").description("구 이름"),
              fieldWithPath("[].districtId").description("구 ID")
            )
          ));
    }

    @Test
    @DisplayName("전체 행정구역 목록 조회 API")
    void getRegions() throws Exception {
        List<RegionResponse> regions = List.of(
          RegionResponse.of(1L, "서울", List.of(
            DistrictResponse.of(2L, "강남구"),
            DistrictResponse.of(3L, "서초구")
          ))
        );
        RegionsResponse response = RegionsResponse.of(regions);

        given(locationService.getRegions()).willReturn(response);

        mockMvc.perform(get("/regions"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.region[0].cityId").value(1L))
          .andExpect(jsonPath("$.region[0].cityName").value("서울"))
          .andExpect(jsonPath("$.region[0].district[0].districtName").value("강남구"))
          .andDo(print())
          .andDo(restDocs.document(
            responseFields(
              fieldWithPath("region[].cityId").description("도시 ID"),
              fieldWithPath("region[].cityName").description("도시 이름"),
              fieldWithPath("region[].district[].districtId").description("구 ID"),
              fieldWithPath("region[].district[].districtName").description("구 이름")
            )
          ));
    }
}
