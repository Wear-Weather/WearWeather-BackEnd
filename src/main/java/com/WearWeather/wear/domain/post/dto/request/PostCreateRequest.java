package com.WearWeather.wear.domain.post.dto.request;

import com.WearWeather.wear.domain.post.entity.Gender;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.tag.dto.TaggableRequest;
import com.WearWeather.wear.global.validation.ValidEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @Size(max = 50)
    private final String content;

    @NotNull
    private final int temperature;

    @ValidEnum(enumClass = Gender.class, message = "올바른 성별 값을 입력해주세요.")
    private final String gender;

    @NotBlank
    private final String city;

    @NotBlank
    private final String district;

    @NotNull
    @Size(max = 2)
    private final Set<Long> weatherTagIds;

    @NotNull
    @Size(max = 2)
    private final Set<Long> temperatureTagIds;

    @NotNull
    private final Long seasonTagId;

    @NotNull(message = "이미지 리스트는 null일 수 없습니다.")
    @NotEmpty(message = "이미지 업로드는 필수입니다.")
    private  List<@NotNull(message = "이미지 ID는 null일 수 없습니다.")
                       @Positive(message = "이미지 ID는 양수여야 합니다.") Long> imageIds = new ArrayList<>();

    @JsonCreator
    public PostCreateRequest(
        @JsonProperty("title") String title,
        @JsonProperty("content") String content,
        @JsonProperty("temperature") int temperature,
        @JsonProperty("gender") String gender,
        @JsonProperty("city") String city,
        @JsonProperty("district") String district,
        @JsonProperty("weatherTagIds") Set<Long> weatherTagIds,
        @JsonProperty("temperatureTagIds") Set<Long> temperatureTagIds,
        @JsonProperty("seasonTagId") Long seasonTagId,
        @JsonProperty("imageIds") List<Long> imageIds
    ) {
        this.title = title;
        this.content = content;
        this.temperature = temperature;
        this.gender = gender;
        this.city = city;
        this.district = district;
        this.weatherTagIds = weatherTagIds;
        this.temperatureTagIds = temperatureTagIds;
        this.seasonTagId = seasonTagId;
        this.imageIds = imageIds;
    }

    public Post toEntity(Long userId,Location location) {
        return Post.builder()
            .userId(userId)
            .title(title)
            .content(content)
            .temperature(temperature)
            .gender(Gender.valueOf(gender))
            .location(location)
            .build();
    }
}
