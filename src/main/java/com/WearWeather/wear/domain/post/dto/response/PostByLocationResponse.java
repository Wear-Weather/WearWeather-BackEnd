package com.WearWeather.wear.domain.post.dto.response;
import com.WearWeather.wear.domain.post.entity.Gender;
import lombok.Builder;

import java.util.List;
import java.util.Map;


@Builder
public record PostByLocationResponse(
        Long postId,
        String thumbnail,
        String seasonTag,
        List<String> weatherTags,
        List<String> temperatureTags,
        boolean likeByUser,
        Gender gender
) {
    public static PostByLocationResponse of(Long postId, String url, Map<String, List<String>> tags, boolean like, Gender gender){
        return PostByLocationResponse.builder()
                .postId(postId)
                .thumbnail(url)
                .seasonTag(tags.get("SEASON").get(0))
                .weatherTags(tags.get("WEATHER"))
                .temperatureTags(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .gender(gender)
                .build();
    }
}
