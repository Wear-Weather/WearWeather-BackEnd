package com.WearWeather.wear.domain.post.dto.response;

import com.WearWeather.wear.domain.post.entity.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class PostsByLocationResponse {
    private final Location location;

    private final List<PostDetailByLocationResponse> posts;

    public static PostsByLocationResponse of(Location location, List<PostDetailByLocationResponse> posts){
        return PostsByLocationResponse.builder()
                .location(location)
                .posts(posts)
                .build();
    }
}
