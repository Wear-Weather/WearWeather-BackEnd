package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class TopLikedPostDetailResponse {

    private final Long postId;
    private final String thumbnail;
    private final LocationResponse location;
    private final Long seasonTagId;
    private final List<Long> weatherTagIds;
    private final List<Long> temperatureTagIds;
    private final boolean likeByUser;

    public static TopLikedPostDetailResponse of(Post post, String url, LocationResponse location, Map<String, List<Long>> tags, boolean like) {
        return TopLikedPostDetailResponse.builder()
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
