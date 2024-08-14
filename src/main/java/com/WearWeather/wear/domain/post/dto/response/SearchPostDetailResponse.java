package com.WearWeather.wear.domain.post.dto.response;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Builder
public class SearchPostDetailResponse {

    private final Long postId;
    private final String thumbnail;
    private final Location location;
    private final Long seasonTagId;
    private final List<Long> weatherTagIds;
    private final List<Long> temperatureTagIds;
    private final boolean likeByUser;

    public static SearchPostDetailResponse of(Post post, String url, Map<String, List<Long>> tags, boolean like){
        return SearchPostDetailResponse.builder()
                .postId(post.getId())
                .thumbnail(url)
                .location(post.getLocation())
                .seasonTagId(tags.get("SEASON").get(0))
                .weatherTagIds(tags.get("WEATHER"))
                .temperatureTagIds(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .build();
    }

}
