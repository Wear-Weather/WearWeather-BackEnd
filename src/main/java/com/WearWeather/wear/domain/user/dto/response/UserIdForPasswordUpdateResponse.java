package com.WearWeather.wear.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class UserIdForPasswordUpdateResponse {

    private final Long userId;

    public static UserIdForPasswordUpdateResponse of(Long userId){
        return UserIdForPasswordUpdateResponse.builder()
                .userId(userId)
                .build();
    }
}
