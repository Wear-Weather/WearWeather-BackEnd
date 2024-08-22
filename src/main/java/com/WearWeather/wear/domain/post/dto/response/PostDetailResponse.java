package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Builder
public class PostDetailResponse {

    private final String nickname;
    private final String date;
    private final String title;
    private final String content;
    private final ImagesResponse images;
    private final LocationResponse location;
    private final Long seasonTagId;
    private final List<Long> weatherTagIds;
    private final List<Long> temperatureTagIds;
    private final boolean likeByUser;
    private final int likedCount;

    public static PostDetailResponse of(String nickname, Post post, ImagesResponse images, LocationResponse location, Map<String, List<Long>> tags, boolean like){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String formattedDateTime = post.getCreateAt().format(formatter);

        return PostDetailResponse.builder()
                .nickname(nickname)
                .date(formattedDateTime)
                .title(post.getTitle())
                .content(post.getContent())
                .location(location)
                .likedCount(post.getLikeCount())
                .images(images)
                .seasonTagId(tags.get("SEASON").get(0))
                .weatherTagIds(tags.get("WEATHER"))
                .temperatureTagIds(tags.get("TEMPERATURE"))
                .likeByUser(like)
                .build();
    }
}
