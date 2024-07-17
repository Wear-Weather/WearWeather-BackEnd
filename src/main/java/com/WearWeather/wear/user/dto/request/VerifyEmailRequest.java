package com.WearWeather.wear.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class VerifyEmailRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private final String email;

}
