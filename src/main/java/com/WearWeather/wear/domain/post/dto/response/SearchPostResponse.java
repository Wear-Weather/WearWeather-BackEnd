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
    private final String seasonTag;
    private final List<String> weatherTags;
    private final List<String> temperatureTags;
    private final boolean likeByUser;
    private final boolean reportPost;

    public static SearchPostResponse of(PostWithLocationName post, String url, Map<String, List<String>> tags, boolean like, boolean report){
        return SearchPostResponse.builder()
                .postId(post.postId())
                .thumbnail(url)
                .location(LocationResponse.of(post.cityName(), post.districtName()))
                .seasonTag(tags.get("SEASON").get(0))
                .weatherTags(tags.get("WEATHER"))
                .temperatureTags(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .reportPost(report)
                .build();
    }

}
