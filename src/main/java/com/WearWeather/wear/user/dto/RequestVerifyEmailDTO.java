package com.WearWeather.wear.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class RequestVerifyEmailDTO {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private final String email;

}
