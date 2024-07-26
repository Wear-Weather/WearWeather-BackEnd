package com.WearWeather.wear.domain.user.dto.response;

import com.WearWeather.wear.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponse {
    private final String email;
    private final String name;
    private final String nickname;

    @JsonCreator
    public UserInfoResponse(
            @JsonProperty("email") String email,
            @JsonProperty("name") String name,
            @JsonProperty("nickname") String nickname) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
    }

    public static UserInfoResponse of(User user) {
        return UserInfoResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .build();
    }

}
