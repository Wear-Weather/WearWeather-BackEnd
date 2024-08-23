package com.WearWeather.wear.domain.post.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Builder
public class SearchPostResponse {

    private final Long postId;
    private final String thumbnail;
    private final LocationResponse location;
    private final Long seasonTagId;
    private final List<Long> weatherTagIds;
    private final List<Long> temperatureTagIds;
    private final boolean likeByUser;
    private final boolean reportPost;

    public static SearchPostResponse of(PostWithLocationName post, String url, Map<String, List<Long>> tags, boolean like, boolean report){
        return SearchPostResponse.builder()
                .postId(post.postId())
                .thumbnail(url)
                .location(LocationResponse.of(post.cityName(), post.districtName()))
                .seasonTagId(tags.get("SEASON").get(0))
                .weatherTagIds(tags.get("WEATHER"))
                .temperatureTagIds(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .reportPost(report)
                .build();
    }

}
