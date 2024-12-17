package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Gender;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record PostByTemperatureResponse(
        Long postId,
        String thumbnail,
        LocationResponse location,
        String seasonTag,
        List<String> weatherTags,
        List<String> temperatureTags,
        boolean likeByUser,
        Gender gender
) {
    public static PostByTemperatureResponse of(Long postId, String url, LocationResponse location, Map<String, List<String>> tags, boolean like, Gender gender){
        return PostByTemperatureResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .location(location)
                .seasonTag(tags.get("SEASON").get(0))
                .weatherTags(tags.get("WEATHER"))
                .temperatureTags(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .gender(gender)
                .build();
    }
}
