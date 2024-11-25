package com.WearWeather.wear.domain.post.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PostsByTemperatureResponse(
    List<PostByTemperatureResponse> posts,
    int total
    ){
    public static PostsByTemperatureResponse of(List<PostByTemperatureResponse> posts, int totalPage){
        return PostsByTemperatureResponse.builder()
                .posts(posts)
                .total(totalPage)
                .build();
    }
}
