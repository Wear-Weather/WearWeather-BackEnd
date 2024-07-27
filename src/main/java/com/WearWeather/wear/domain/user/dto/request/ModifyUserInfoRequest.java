package com.WearWeather.wear.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ModifyUserInfoRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    private final String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    private final String nickname;
    @JsonCreator
    public ModifyUserInfoRequest(
            @JsonProperty("password") String password,
            @JsonProperty("nickname") String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}
