package com.WearWeather.wear.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FindUserPasswordRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private final String email;

    @NotBlank(message = "이름은 필수입니다.")
    private final String name;

    @NotBlank(message = "닉네임은 필수입니다.")
    private final String nickname;

}
