package com.WearWeather.wear.domain.post.dto.request;

import com.WearWeather.wear.domain.post.entity.SortType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record PostsByFiltersRequest (

    @NotNull(message = "페이지 값은 필수입니다.")
    int page,
    @NotNull(message = "사이즈 값은 필수입니다.")
    @Min(value = 1, message = "사이즈는 1이상이여야 합니다.")
    int size,

    @Valid
    List<LocationRequest> location,

    List<Long> seasonTagIds,
    List<Long> weatherTagIds,
    List<Long> temperatureTagIds,

    @NotNull(message = "정렬 방법은 필수입니다.")
    SortType sort
){
}
