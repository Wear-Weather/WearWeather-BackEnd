package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Post;
import java.util.List;
import java.util.Map;
import lombok.Builder;


@Builder
public record TopLikedPostResponse(
        Long postId,
        String thumbnail,
        LocationResponse location,
        String seasonTag,
        List<String> weatherTags,
        List<String> temperatureTags,
        boolean likeByUser
) {
    public static TopLikedPostResponse of(Post post, String url, LocationResponse location, Map<String, List<String>> tags, boolean like) {
        return TopLikedPostResponse.builder()
            .postId(post.getId())
            .thumbnail(url)
            .location(location)
            .seasonTag(tags.get("SEASON").get(0))
            .weatherTags(tags.get("WEATHER"))
            .temperatureTags(tags.get("TEMPERATURE"))
            .likeByUser(like)
            .build();
    }
}
