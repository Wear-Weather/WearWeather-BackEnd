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
        String seasonTag,
        List<String> weatherTags,
        List<String> temperatureTags,
        boolean likeByUser,
        boolean reportPost
) {
    public static LikedPostByMeResponse of(Long postId, String url, LocationResponse location, Map<String, List<String>> tags, boolean likeByUser, boolean report){
        return LikedPostByMeResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .location(location)
                .seasonTag(tags.get("SEASON").get(0))
                .weatherTags(tags.get("WEATHER"))
                .temperatureTags(tags.get("TEMPERATURE"))
                .likeByUser(likeByUser)
                .reportPost(report)
                .build();
    }
}
