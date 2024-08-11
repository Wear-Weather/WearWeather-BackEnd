package com.WearWeather.wear.domain.post.dto.request;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostsByLocationRequest {

    @NotNull(message = "페이지 값은 필수입니다.")
    private final int page;
    @NotNull(message = "사이즈 값은 필수입니다.")
    @Min(value = 1, message = "사이즈는 1이상이여야 합니다.")
    private final int size;

    @Valid
    private final Location location;

    @NotBlank(message = "정렬 방법은 필수입니다.")
    private final SortType sort;

    @JsonCreator
    public PostsByLocationRequest(
            @JsonProperty("page") int page,
            @JsonProperty("size") int size,
            @JsonProperty("location") Location location,
            @JsonProperty("sort") SortType sort
    ){
        this.page = page;
        this.size = size;
        this.location = location;
        this.sort = sort;
    }

}
