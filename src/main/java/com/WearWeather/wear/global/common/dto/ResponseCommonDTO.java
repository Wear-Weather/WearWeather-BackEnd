package com.WearWeather.wear.global.common.dto;

import com.WearWeather.wear.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseCommonDTO {

    private final boolean success;
    private final String message;

}
