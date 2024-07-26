package com.WearWeather.wear.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ModifyUserPasswordRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    private final String password;

    @JsonCreator
    public ModifyUserPasswordRequest(
            @JsonProperty("password") String password) {
        this.password = password;
    }
}
