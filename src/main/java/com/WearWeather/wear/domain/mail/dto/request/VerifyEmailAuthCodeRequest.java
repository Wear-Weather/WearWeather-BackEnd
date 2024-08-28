package com.WearWeather.wear.domain.mail.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class VerifyEmailAuthCodeRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private final String email;

    @NotBlank(message = "인증코드는 필수입니다.")
    private final String code;

    @JsonCreator
    public VerifyEmailAuthCodeRequest(
            @JsonProperty("email") String email,
            @JsonProperty("code") String code
    ) {
        this.email = email;
        this.code = code;
    }
}
