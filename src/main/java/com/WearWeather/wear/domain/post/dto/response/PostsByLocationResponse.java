package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostsByLocationResponse(
        LocationResponse location,
        List<PostByLocationResponse> posts,
        int total
){
    public static PostsByLocationResponse of(LocationResponse location, List<PostByLocationResponse> posts, int totalPage){
        return PostsByLocationResponse.builder()
                .location(location)
                .posts(posts)
                .total(totalPage)
                .build();
    }
}
