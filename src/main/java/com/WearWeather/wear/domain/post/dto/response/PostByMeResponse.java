package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Gender;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PostByMeResponse(
        Long postId,
        String thumbnail,
        LocationResponse location,
        String seasonTag,
        List<String> weatherTags,
        List<String> temperatureTags,
        boolean likeByUser,
        boolean reportPost,
        Gender gender
) {
    public static PostByMeResponse of(Long postId, String url, LocationResponse location, Map<String, List<String>> tags, boolean like, boolean report, Gender gender){
        return PostByMeResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .location(location)
                .seasonTag(tags.get("SEASON").get(0))
                .weatherTags(tags.get("WEATHER"))
                .temperatureTags(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .reportPost(report)
                .gender(gender)
                .build();
    }
}
