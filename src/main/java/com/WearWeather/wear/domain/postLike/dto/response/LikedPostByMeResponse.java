package com.WearWeather.wear.domain.postLike.dto.response;

import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record LikedPostByMeResponse(

        Long postId,
        String thumbnail,
        LocationResponse location,
        Long seasonTagId,
        List<Long> weatherTagIds,
        List<Long> temperatureTagIds,
        boolean likeByUser,
        boolean reportPost
) {
    public static LikedPostByMeResponse of(Long postId, String url, LocationResponse location, Map<String, List<Long>> tags, boolean likeByUser, boolean report){
        return LikedPostByMeResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .location(location)
                .seasonTagId(tags.get("SEASON").get(0))
                .weatherTagIds(tags.get("WEATHER"))
                .temperatureTagIds(tags.get("TEMPERATURE"))
                .likeByUser(likeByUser)
                .reportPost(report)
                .build();
    }
}
