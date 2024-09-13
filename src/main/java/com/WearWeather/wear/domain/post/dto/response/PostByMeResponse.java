package com.WearWeather.wear.domain.post.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PostByMeResponse(
        Long postId,
        String thumbnail,
        LocationResponse location,
        String seasonTag,
        List<String> weatherTags,
        List<String> temperatureTags,
        boolean reportPost
) {
    public static PostByMeResponse of(Long postId, String url, LocationResponse location, Map<String, List<String>> tags, boolean report){
        return PostByMeResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .location(location)
                .seasonTag(tags.get("SEASON").get(0))
                .weatherTags(tags.get("WEATHER"))
                .temperatureTags(tags.get("TEMPERATURE"))
                .reportPost(report)
                .build();
    }
}
