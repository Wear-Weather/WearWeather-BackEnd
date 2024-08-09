package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class PostDetailResponse {

    private final String nickname;
    private final String createAt;
    private final String title;
    private final String content;
    private final List<String> images;
    private final Location location;
    private final Long seasonTag;
    private final List<Long> weatherTags;
    private final List<Long> temperatureTags;
    private final boolean likeByUser;
    private final int likedCount;

    public static PostDetailResponse of(String nickname, Post post, List<String> images, Long seasonTag, List<Long> weatherTags, List<Long> temperatureTags, boolean like){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String formattedDateTime = post.getCreateAt().format(formatter);

        return PostDetailResponse.builder()
                .nickname(nickname)
                .createAt(formattedDateTime)
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .likedCount(post.getLikeCount())
                .images(images)
                .seasonTag(seasonTag)
                .weatherTags(weatherTags)
                .temperatureTags(temperatureTags)
                .likeByUser(like)
                .build();
    }
}
