package com.WearWeather.wear.domain.post.dto.response;
import lombok.Builder;

import java.util.List;
import java.util.Map;


@Builder
public record PostByLocationResponse(
        Long postId,
        String thumbnail,
        Long seasonTagId,
        List<Long> weatherTagIds,
        List<Long> temperatureTagIds,
        boolean likeByUser,
        boolean reportPost
) {
    public static PostByLocationResponse of(Long postId, String url, Map<String, List<Long>> tags, boolean like, boolean report){
        return PostByLocationResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .seasonTagId(tags.get("SEASON").get(0))
                .weatherTagIds(tags.get("WEATHER"))
                .temperatureTagIds(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .reportPost(report)
                .build();
    }
}
