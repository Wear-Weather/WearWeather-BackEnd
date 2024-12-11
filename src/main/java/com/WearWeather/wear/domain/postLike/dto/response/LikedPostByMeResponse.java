package com.WearWeather.wear.domain.postLike.dto.response;

import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import com.WearWeather.wear.domain.post.entity.Gender;
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
        Gender gender
) {
    public static LikedPostByMeResponse of(Long postId, String url, LocationResponse location, Map<String, List<String>> tags, boolean likeByUser, Gender gender){
        return LikedPostByMeResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .location(location)
                .seasonTag(tags.get("SEASON").get(0))
                .weatherTags(tags.get("WEATHER"))
                .temperatureTags(tags.get("TEMPERATURE"))
                .likeByUser(likeByUser)
                .gender(gender)
                .build();
    }
}
