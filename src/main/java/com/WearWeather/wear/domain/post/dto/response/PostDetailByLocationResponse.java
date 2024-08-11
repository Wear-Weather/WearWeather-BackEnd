package com.WearWeather.wear.domain.post.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    public static PostDetailByLocationResponse of(Long postId, String url, Long seasonTagId, List<Long> weatherTagIds, List<Long> temperatureTagIds, boolean like){
        return PostDetailByLocationResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .seasonTagId(seasonTagId)
                .weatherTagIds(weatherTagIds)
                .temperatureTagIds(temperatureTagIds)
                .likeByUser(like)
                .build();
    }

}
