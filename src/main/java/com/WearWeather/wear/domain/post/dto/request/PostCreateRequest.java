package com.WearWeather.wear.domain.post.dto.request;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.tag.entity.Season;
import com.WearWeather.wear.domain.tag.entity.Temperature;
import com.WearWeather.wear.domain.tag.entity.Weather;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateRequest {

    @NotBlank
    @Size(max = 50)
    private final String title;

    @NotBlank
    @Size(max = 50)
    private final String content;

    private final Location location;

    // 최대 1개 선택이기 때문에 Set 사용 X
    private final Season seasonTag;

    private final Set<Weather> weatherTags;
    private final Set<Temperature> temperatureTags;

    private final List<Long> imageId = new ArrayList<>();

    @JsonCreator
    public PostCreateRequest(
        @JsonProperty("title") String title,
        @JsonProperty("content") String content,
        @JsonProperty("location") Location location,
        @JsonProperty("seasonTag") Season seasonTag,
        @JsonProperty("weatherTag") Set<Weather> weatherTags,
        @JsonProperty("temperatureTag") Set<Temperature> temperatureTags
    ) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.seasonTag = seasonTag;
        this.weatherTags = weatherTags;
        this.temperatureTags = temperatureTags;
    }

    public Post toEntity(Long userId) {
        return Post.builder()
            .userId(userId)
            .title(title)
            .content(content)
            .location(location)
            .build();
    }

    public Map<String, Set<String>> getTagsMap() {
        Map<String, Set<String>> tagsMap = new HashMap<>();
        tagsMap.put("weather", weatherTags.stream().map(Enum::name).collect(Collectors.toSet()));
        tagsMap.put("temperature", temperatureTags.stream().map(Enum::name).collect(Collectors.toSet()));
        tagsMap.put("season", Set.of(seasonTag.name()));
        return tagsMap;
    }
}