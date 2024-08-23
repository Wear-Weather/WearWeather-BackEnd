package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Post;
import java.util.List;
import java.util.Map;
import lombok.Builder;


@Builder
public record TopLikedPostResponse(
        Long postId,
        String thumbnail,
        LocationResponse location,
        Long seasonTagId,
        List<Long> weatherTagIds,
        List<Long> temperatureTagIds,
        boolean likeByUser
) {
    public static TopLikedPostResponse of(Post post, String url, LocationResponse location, Map<String, List<Long>> tags, boolean like) {
        return TopLikedPostResponse.builder()
            .postId(post.getId())
            .thumbnail(url)
            .location(location)
            .seasonTagId(tags.get("SEASON").get(0))
            .weatherTagIds(tags.get("WEATHER"))
            .temperatureTagIds(tags.get("TEMPERATURE"))
            .likeByUser(like)
            .build();
    }
}
