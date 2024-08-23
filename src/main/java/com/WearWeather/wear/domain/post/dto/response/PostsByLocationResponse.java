package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostsByLocationResponse(
        LocationResponse location,
        List<PostByLocationResponse> posts
){
    public static PostsByLocationResponse of(LocationResponse location, List<PostByLocationResponse> posts){
        return PostsByLocationResponse.builder()
                .location(location)
                .posts(posts)
                .build();
    }
}
