package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class PostsByFiltersResponse {
    private final List<SearchPostDetailResponse> posts;

    public static PostsByFiltersResponse of(List<SearchPostDetailResponse> posts){
        return PostsByFiltersResponse.builder()
                .posts(posts)
                .build();
    }
}
