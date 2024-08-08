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
    private final String seasonTag;
    private final List<String> weatherTags;
    private final List<String> temperatureTags;
    private final boolean likeByUser;
    private final int likedCount;

    public static PostDetailResponse of(String nickname, Post post, List<String> images, String seasonTag, List<String> weatherTags, List<String> temperatureTags, boolean like){

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
