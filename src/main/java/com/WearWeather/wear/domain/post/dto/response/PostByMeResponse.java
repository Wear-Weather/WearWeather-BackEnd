package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PostByMeResponse(
        Long postId,
        String thumbnail,
        LocationResponse location,
        Long seasonTagId,
        List<Long> weatherTagIds,
        List<Long> temperatureTagIds,
        boolean reportPost
) {
    public static PostByMeResponse of(Long postId, String url, LocationResponse location, Map<String, List<Long>> tags, boolean report){
        return PostByMeResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .location(location)
                .seasonTagId(tags.get("SEASON").get(0))
                .weatherTagIds(tags.get("WEATHER"))
                .temperatureTagIds(tags.get("TEMPERATURE"))
                .reportPost(report)
                .build();
    }
}
