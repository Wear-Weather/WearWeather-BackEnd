package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostsByMeResponse(
        List<PostByMeResponse> myPosts,
        int total
) {
    public static PostsByMeResponse of(List<PostByMeResponse> posts, int totalPage){
        return PostsByMeResponse.builder()
                .myPosts(posts)
                .total(totalPage)
                .build();
    }
}
