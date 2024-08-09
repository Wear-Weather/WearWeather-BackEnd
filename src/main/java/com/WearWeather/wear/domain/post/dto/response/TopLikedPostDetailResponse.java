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
    private final Long seasonTag;
    private final List<Long> weatherTags;
    private final List<Long> temperatureTags;
    private final boolean likeByUser;

    public static TopLikedPostDetailResponse of(Post post, String url, Long seasonTag, List<Long> weatherTags, List<Long> temperatureTags, boolean like){
        return TopLikedPostDetailResponse.builder()
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
