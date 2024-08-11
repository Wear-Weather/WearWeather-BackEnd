package com.WearWeather.wear.domain.post.dto.response;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class TopLikedPostDetailResponse {

    private final Long postId;
    private final String thumbnail;
    private final Location location;
    private final Long seasonTagId;
    private final List<Long> weatherTagIds;
    private final List<Long> temperatureTagIds;
    private final boolean likeByUser;

    public static TopLikedPostDetailResponse of(Post post, String url, Long seasonTagId, List<Long> weatherTagIds, List<Long> temperatureTagIds, boolean like){
        return TopLikedPostDetailResponse.builder()
                .postId(post.getPostId())
                .thumbnail(url) //TODO : URL 만드는 법 확인
                .location(post.getLocation())
                .seasonTagId(seasonTagId)
                .weatherTagIds(weatherTagIds)
                .temperatureTagIds(temperatureTagIds)
                .likeByUser(like)
                .build();
    }

}
