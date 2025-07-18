package com.WearWeather.wear.domain.post.dto.request;

import com.WearWeather.wear.domain.post.entity.Gender;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.tag.dto.TaggableRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostUpdateRequest implements PostImageRequest, TaggableRequest {

    @NotBlank
    @Size(max = 50)
    private final String title;

    @Size(max = 50)
    private final String content;

    @NotBlank
    private final String city;

    @NotBlank
    private final String district;

    private final Gender gender;

    @NotNull
    @Size(max = 2)
    private final Set<Long> weatherTagIds;

    @NotNull
    @Size(max = 2)
    private final Set<Long> temperatureTagIds;

    @NotNull
    private final Long seasonTagId;

    @NotEmpty(message = "이미지 업로드는 필수입니다.")
    private List<Long> imageIds = new ArrayList<>();

    @JsonCreator
    public PostUpdateRequest(
        @JsonProperty("title") String title,
        @JsonProperty("content") String content,
        @JsonProperty("city") String city,
        @JsonProperty("district") String district,
        @JsonProperty("gender") Gender gender,
        @JsonProperty("weatherTagIds") Set<Long> weatherTagIds,
        @JsonProperty("temperatureTagIds") Set<Long> temperatureTagIds,
        @JsonProperty("seasonTagId") Long seasonTagId,
        @JsonProperty("imageIds") List<Long> imageIds
    ) {
        this.title = title;
        this.content = content;
        this.city = city;
        this.district = district;
        this.gender = gender;
        this.weatherTagIds = weatherTagIds;
        this.temperatureTagIds = temperatureTagIds;
        this.seasonTagId = seasonTagId;
        this.imageIds = imageIds;
    }
}
