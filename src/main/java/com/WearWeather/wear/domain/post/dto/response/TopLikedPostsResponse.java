package com.WearWeather.wear.domain.post.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TopLikedPostsResponse {

    private final List<TopLikedPostDetailResponse> topLikedPostDetailResponse;
}
