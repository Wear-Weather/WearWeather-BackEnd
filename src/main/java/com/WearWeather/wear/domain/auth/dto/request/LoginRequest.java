package com.WearWeather.wear.domain.auth.dto.request;

import com.WearWeather.wear.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class LoginRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private final String email;

    @NotBlank
    @Size(min = 3, max = 100)
    private final String password;

    @JsonCreator
    public LoginRequest(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    public User toEntity() {
        return User.builder()
            .email(email)
            .password(password)
            .build();
    }
}
