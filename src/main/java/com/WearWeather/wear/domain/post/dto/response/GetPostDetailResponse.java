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
public class GetPostDetailResponse {

    private final Long postId;
    private final String thumbnail;
    private final Location location;
    private final String seasonTag;
    private final List<String> weatherTags;
    private final List<String> temperatureTags;
    private final boolean likeByUser;

    public static GetPostDetailResponse of(Post post, String url, String seasonTag, List<String> weatherTags, List<String> temperatureTags, boolean like){
        return GetPostDetailResponse.builder()
                .postId(post.getPostId())
                .thumbnail(url) //TODO : URL 만드는 법 확인
                .location(post.getLocation())
                .seasonTag(seasonTag)
                .weatherTags(weatherTags)
                .temperatureTags(temperatureTags)
                .likeByUser(like)
                .build();
    }

}
