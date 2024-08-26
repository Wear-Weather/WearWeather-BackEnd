package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
public record TopLikedPostsResponse(
        List<TopLikedPostResponse> topLikedPosts
) {
    public static TopLikedPostsResponse of(List<TopLikedPostResponse> topLikedPosts){
        return TopLikedPostsResponse.builder()
                .topLikedPosts(topLikedPosts)
                .build();
    }
}
