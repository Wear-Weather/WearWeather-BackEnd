package com.WearWeather.wear.fixture;

import com.WearWeather.wear.domain.post.dto.request.LocationRequest;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.ImageDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.ImagesResponse;
import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostByMeResponse;
import com.WearWeather.wear.domain.post.dto.response.PostByTemperatureResponse;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.PostWithLocationName;
import com.WearWeather.wear.domain.post.dto.response.PostsByFiltersResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByMeResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByTemperatureResponse;
import com.WearWeather.wear.domain.post.dto.response.SearchPostResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostResponse;
import com.WearWeather.wear.domain.post.entity.Gender;
import com.WearWeather.wear.domain.post.entity.SortType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PostFixture {

    public static final String title = "오늘의 코디";
    public static final String content = "설명";
    public static final int temperature = 20;
    public static final String gender = "FEMALE";
    public static final String city = "서울";
    public static final String district = "강남구";

    public static final Set<Long> weatherTagIds = Set.of(1L);
    public static final Set<Long> temperatureTagIds = Set.of(2L);
    public static final List<Long> seasonTagIds = List.of(2L);


    public static final List<Long> weatherTagIdList = List.of(2L);
    public static final List<Long> temperatureTagIdList = List.of(2L);


    public static final Long seasonTagId = 3L;
    public static final List<Long> imageIds = List.of(10L);

    public static PostCreateRequest createPostRequest() {
        return PostCreateRequest.builder()
          .title(title)
          .content(content)
          .temperature(temperature)
          .gender(gender)
          .city(city)
          .district(district)
          .weatherTagIds(weatherTagIds)
          .temperatureTagIds(temperatureTagIds)
          .seasonTagId(seasonTagId)
          .imageIds(imageIds)
          .build();
    }

    public static PostUpdateRequest updatePostRequest() {
        return PostUpdateRequest.builder()
          .title(title)
          .content(content)
          .city(city)
          .district(district)
          .gender(Gender.FEMALE)
          .weatherTagIds(weatherTagIds)
          .temperatureTagIds(temperatureTagIds)
          .seasonTagId(seasonTagId)
          .imageIds(imageIds)
          .build();
    }

    public static PostDetailResponse getPostDetailResponse() {
        return PostDetailResponse.builder()
          .nickname("건희")
          .date("2024.01.01 10:00")
          .title(title)
          .content(content)
          .images(ImagesResponse.of(List.of(
            ImageDetailResponse.of(1L, "https://image.url/1.jpg"),
            ImageDetailResponse.of(2L, "https://image.url/2.jpg")
          )))
          .location(LocationResponse.of(city, district))
          .seasonTag("봄")
          .weatherTags(List.of("맑음"))
          .temperatureTags(List.of("선선함"))
          .likeByUser(true)
          .likedCount(10)
          .reportPost(false)
          .build();
    }

    public static PostsByLocationResponse postsByLocationResponse() {
        return PostsByLocationResponse.of(
          LocationResponse.of(city, district),
          List.of(
            PostByLocationResponse.of(
              1L,
              "https://image.url/thumbnail.jpg",
              Map.of(
                "SEASON", List.of("봄"),
                "WEATHER", List.of("맑음"),
                "TEMPERATURE", List.of("선선함")
              ),
              true
            )
          ),
          5
        );
    }

    public static PostsByFiltersRequest postsByFiltersRequest() {
        return PostsByFiltersRequest.builder()
          .page(0)
          .size(10)
          .location(List.of(new LocationRequest(1L, 2L))) // 예시로 cityId = 1, districtId = 2
          .seasonTagIds(List.of(seasonTagId))
          .weatherTagIds(List.copyOf(weatherTagIds))
          .temperatureTagIds(List.copyOf(temperatureTagIds))
          .gender(gender) // 문자열 "FEMALE"
          .sort(SortType.LATEST)
          .build();
    }


    public static PostsByFiltersResponse getPostsByFiltersResponse() {
        return PostsByFiltersResponse.of(
          List.of(
            SearchPostResponse.of(
              new PostWithLocationName(
                1L,
                10L,
                city,
                district,
                Gender.FEMALE
              ),
              "https://image.url/thumbnail.jpg",
              Map.of(
                "SEASON", List.of("봄"),
                "WEATHER", List.of("맑음"),
                "TEMPERATURE", List.of("선선함")
              ),
              true,
              Gender.FEMALE
            )
          ),
          3
        );
    }

    public static PostsByTemperatureResponse getPostsByTemperatureResponse() {
        return PostsByTemperatureResponse.of(
          18, 22,
          List.of(PostByTemperatureResponse.of(
            1L,
            "https://image.url/1.jpg",
            LocationResponse.of("서울", "강남구"),
            Map.of(
              "SEASON", List.of("봄"),
              "WEATHER", List.of("맑음"),
              "TEMPERATURE", List.of("선선함")
            ),
            true
          )),
          3
        );
    }

    public static PostsByMeResponse getPostsByMeResponse() {
        return PostsByMeResponse.of(
          List.of(PostByMeResponse.of(
            1L,
            "https://image.url/1.jpg",
            LocationResponse.of("서울", "강남구"),
            Map.of(
              "SEASON", List.of("봄"),
              "WEATHER", List.of("맑음"),
              "TEMPERATURE", List.of("선선함")
            ),
            true,
            false
          )),
          2
        );
    }

    public static List<TopLikedPostResponse> getTopLikedPostResponses() {
        return List.of(
          TopLikedPostResponse.builder()
            .postId(1L)
            .thumbnail("https://image.com/1.jpg")
            .location(new LocationResponse("서울", "강남구"))
            .seasonTag("겨울")
            .weatherTags(List.of("눈", "흐림"))
            .temperatureTags(List.of("0~5도"))
            .likeByUser(true)
            .build()
        );
    }
}
