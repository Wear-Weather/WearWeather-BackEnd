package com.WearWeather.wear.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ModifyUserPasswordRequest {

    @NotNull(message = "회원번호는 필수입니다.")
    private final Long userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private final String password;

    @JsonCreator
    public ModifyUserPasswordRequest(
            @JsonProperty("userId") Long userId,
            @JsonProperty("password") String password) {
        this.userId = userId;
        this.password = password;
    }
}
