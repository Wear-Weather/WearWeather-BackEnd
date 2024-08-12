package com.WearWeather.wear.domain.post.dto.request;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.tag.dto.TaggableRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateRequest implements PostImageRequest, TaggableRequest {

    @NotBlank
    @Size(max = 50)
    private final String title;

    @NotBlank
    @Size(max = 50)
    private final String content;

    @Valid
    private final Location location;

    @NotBlank
    @Size(max = 2)
    private final Set<Long> weatherTagIds;

    @NotBlank
    @Size(max = 2)
    private final Set<Long> temperatureTagIds;

    @NotNull
    private final Long seasonTagId;

    private final List<Long> imageId = new ArrayList<>();

    @JsonCreator
    public PostCreateRequest(
        @JsonProperty("title") String title,
        @JsonProperty("content") String content,
        @JsonProperty("location") Location location,
        @JsonProperty("weatherTagIds") Set<Long> weatherTagIds,
        @JsonProperty("temperatureTagIds") Set<Long> temperatureTagIds,
        @JsonProperty("seasonTagId") Long seasonTagId
    ) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.weatherTagIds = weatherTagIds;
        this.temperatureTagIds = temperatureTagIds;
        this.seasonTagId = seasonTagId;
    }

    public Post toEntity(Long userId) {
        return Post.builder()
            .userId(userId)
            .title(title)
            .content(content)
            .location(location)
            .build();
    }
}
