package com.WearWeather.wear.domain.post.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PostsByTemperatureResponse(
    int tmpRangeStart,
    int tmpRangeEnd,
    List<PostByTemperatureResponse> posts,
    int total
    ){
    public static PostsByTemperatureResponse of(int tmpRangeStart, int tmpRangeEnd, List<PostByTemperatureResponse> posts, int totalPage){
        return PostsByTemperatureResponse.builder()
                .tmpRangeStart(tmpRangeStart)
                .tmpRangeEnd(tmpRangeEnd)
                .posts(posts)
                .total(totalPage)
                .build();
    }
}
