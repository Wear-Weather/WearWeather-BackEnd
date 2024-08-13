package com.WearWeather.wear.domain.post.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Builder
public class PostDetailByLocationResponse {

    private final Long postId;
    private final String thumbnail;
    private final Long seasonTagId;
    private final List<Long> weatherTagIds;
    private final List<Long> temperatureTagIds;
    private final boolean likeByUser;

    public static PostDetailByLocationResponse of(Long postId, String url, Map<String, List<Long>> tags, boolean like){
        return PostDetailByLocationResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .seasonTagId(tags.get("SEASON").get(0))
                .weatherTagIds(tags.get("WEATHER"))
                .temperatureTagIds(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .build();
    }

}
