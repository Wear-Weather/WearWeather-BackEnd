package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostsByMeResponse(
        List<PostByMeResponse> myPosts
) {
    public static PostsByMeResponse of(List<PostByMeResponse> posts){
        return PostsByMeResponse.builder()
                .myPosts(posts)
                .build();
    }
}
