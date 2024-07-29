package com.WearWeather.wear.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FindUserEmailRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private final String name;

    @NotBlank(message = "닉네임은 필수입니다.")
    private final String nickname;

    @JsonCreator
    public FindUserEmailRequest(
            @JsonProperty("name") String name,
            @JsonProperty("nickname") String nickname) {
        this.name = name;
        this.nickname = nickname;
    }
}
