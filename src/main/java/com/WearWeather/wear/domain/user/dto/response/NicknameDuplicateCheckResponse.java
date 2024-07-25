package com.WearWeather.wear.domain.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NicknameDuplicateCheckResponse {

    private final boolean isAvailable;
    private final String message;

}
