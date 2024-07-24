package com.WearWeather.wear.auth.dto.response;

import com.WearWeather.wear.user.entity.Role;
import com.WearWeather.wear.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private long userId;
    private String email;
    private String name;
    private String nickName;
    private boolean isSocial;
    private Role role;
    private String accessToken;
    private String refreshToken;

    public static LoginResponse of(User user, String at, String rt) {
        return LoginResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .nickName(user.getNickname())
            .isSocial(user.isSocial())
            .role(user.getRole())
            .accessToken(at)
            .refreshToken(rt)
            .build();
    }
}

