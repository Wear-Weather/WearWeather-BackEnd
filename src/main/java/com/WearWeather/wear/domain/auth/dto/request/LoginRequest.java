package com.WearWeather.wear.domain.auth.dto.request;

import com.WearWeather.wear.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String email;

    @NotBlank
    @Size(min = 3, max = 100)
    private String password;

    public User toEntity() {
        return User.builder()
            .email(email)
            .password(password)
            .build();
    }
}
